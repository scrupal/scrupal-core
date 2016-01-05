/**********************************************************************************************************************
 * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/

package scrupal.core

import akka.http.scaladsl.model.MediaType

import java.io.InputStream

import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

/** Encapsulation of an Action's Response
  *
  * Responses are generated from an action processing a request. They encapsulate a payload and a disposition. The
  * disposition provides a quick summary of the result while the payload provides access to the actual resulting
  * information.
  *
  * @tparam PT The Payload Type
  */
sealed trait Response[PT <: Content[_]] {
  /** The payload content of the response
    *
    * @return a Content[_] object that provides the payload of the response
    */
  def payload : PT

  /** The disposition of the response
    *
    * @return A Disposition that indicates the way in which the action succeeded or failed
    */
  def disposition: Disposition

  /** Type Of Media Returned.
    *
    * This is a ContentType value from Akka-Http. It indicates what kind of media and character encoding is being
    * returned by the payload. It is extracted, by default, directly from the payload
    *
    * @return A ContentType corresponding to the content type of `payload`
    */
  def mediaType : MediaType = payload.mediaType

  /** Convert Payload to Enumerator
    *
    * This allows the payload of type PT to be converted into a standardized type for serialization to a stream.
    * It converts the content into an Enumerator of Array[Byte].
    *
    * @return The content as an Enumerated byte array
    */
  def toEnumerator(implicit ec: ExecutionContext) : Enumerator[Array[Byte]] = payload.toEnumerator(ec)

  /** Convert Payload To EnumeratedResponse
    * This allows for there to be a common ground in all responses. An EnumeratedResponse can be converted
    * directly to a Play Result
    * @param ec The execution context to use in generating the response
    * @return
    */
  def toEnumeratedResponse(implicit ec: ExecutionContext) : EnumeratedResponse = {
    Response(payload.toEnumerator(ec), payload.mediaType, disposition)
  }
}

/** Companion Object For Response
  * This provides a variety of ways to construct Response objects
  */
object Response {
  def apply[C <: Content[_]](theContent: C, theDisposition : Disposition = Successful) : Response[C] = {
    new Response[C] { val payload : C = theContent ; val disposition : Disposition = theDisposition }
  }
  def apply(content: Enumerator[Array[Byte]], mediaType : MediaType,
            disposition: Disposition) : EnumeratedResponse = {
    new EnumeratedResponse(EnumeratedContent(content, mediaType), disposition)
  }
  def apply(content: Seq[Enumerator[Array[Byte]]], mediaType : MediaType,
            disposition : Disposition) : Response[EnumeratorsContent] = {
    apply(EnumeratorsContent(content, mediaType), disposition)
  }
  def apply(content : InputStream, mediaType : MediaType,
            disposition : Disposition) : Response[StreamContent] = {
    apply(StreamContent(content, mediaType), disposition)
  }
  def apply(content : Array[Byte], mediaType : MediaType,
            disposition : Disposition) : Response[OctetsContent] = {
    apply(OctetsContent(content, mediaType), disposition)
  }

  /** Response with a simple text string payload.
    *
    * This kind of Response just encapsulates a String and defaults its ContentType to text/plain(UTF-8).
    *
    * @param content The string content of the response
    * @param disposition The disposition of the response.
    * @return The Response
    */
  def apply(content : String, disposition : Disposition) : Response[TextContent] = {
    apply(TextContent(content), disposition)
  }

  /** Response with an HTMLFormat payload.
    *
    * This kind of response just encapsulates a Twirl Html value and defaults its ContentType to text/html.
    *
    * @param content The Html payload of the response.
    * @param disposition The disposition of the response.
    * @return The Response
    */
  def apply(content : Html, disposition : Disposition) : Response[HtmlContent] = {
    apply(HtmlContent(content), disposition)
  }

  /** Response with JSON payload.
    *
    * This kind of response encapsulates a JSON value and defaults it ContentType to application/json.
    *
    * @param content The JsValue payload of the response.
    * @param disposition The disposition of the response.
    * @return The Response
    */
  def apply(content : JsValue, disposition : Disposition) : Response[JsonContent] = {
    apply(JsonContent(content), disposition)
  }

  /** Response with a Throwable payload.
    *
    * This kind of response just encapsulates an error embodied by a Throwable. This is how hard errors are returned from
    * a result. Note that the Disposition is always an Exception
    *
    * @param content The error that occurred
    */
  def apply(content: Throwable) : Response[ThrowableContent]= {
    apply(ThrowableContent(content), Exception)
  }

  /** Generate a response safely, handling exceptions
    *
    * This function takes a by-name argument that produces a Response and returns it. If the argument is a
    * function that could fail with an exception, then an Exception response is returned with a ThrowableContent
    * wrapping the exception
    *
    * @param f A by-name argument that produces a Response
    * @return The Response, even if there's an exception
    */
  def safely[PT <: Content[_]]( f: ⇒ Response[PT] ) : Response[_] = {
    try {
      f
    } catch {
      case x : Throwable ⇒
        Response(ThrowableContent(x), Exception)
    }
  }
}

/** No-operation Response
  * A case object for returning nothing as a response
  */
case object NoopResponse extends Response[EmptyContent.type] {
  lazy val payload = EmptyContent
  val disposition = Received
}

/** Response For Unimplemented Functionality
  *
  * @param what The thing that is not imnplemented
  */
case class UnimplementedResponse(what: String) extends Response[TextContent] {
  val payload = TextContent(what)
  def disposition = Unimplemented
  def formatted = s"${disposition.id.name}: $what"
  override def toEnumerator(implicit ec: ExecutionContext) = Enumerator(formatted.getBytes(utf8))
}

/** Response With An Enumerator
  * This is used to capture responses of arbitrary type as a single type since all Reponses can be
  * converted into this kind of response
  *
  * @param payload the EnumeratedContent of the response
  * @param disposition the Disposition of the response
  */
case class EnumeratedResponse(
 payload : EnumeratedContent,
 disposition : Disposition = Successful
) extends Response[EnumeratedContent]
