package scrupal.core

import java.io.InputStream

import akka.http.scaladsl.model.{MediaTypes, MediaType}
import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json._
import play.twirl.api.{Html, Txt}

import scala.concurrent.{Future, Await, ExecutionContext}

/** Generic Content Representation
  * Content can come in many forms, as defined by the type parameter T. What they all need in common is a
  * mediaType to indicate the kind of content, and a way to enumerate the content as an array of bytes.
  *
  * @tparam T
  */
trait Content[T] {
  def content : T
  def mediaType : MediaType
  def toEnumerator(implicit ec: ExecutionContext) : Enumerator[Array[Byte]]
  def toBytes(implicit ec: ExecutionContext) : Future[Array[Byte]]
}

object Content {
  def apply(payload: Array[Byte], mediaType : MediaType) : Content[_] = {
    mediaType match {
      case MediaTypes.`application/octet-stream` ⇒
        OctetsContent(payload)
      case MediaTypes.`text/plain` ⇒
        TextContent(new String(payload,utf8))
      case MediaTypes.`text/html` ⇒
        HtmlContent(Html(new String(payload,utf8)))
      case MediaTypes.`application/json` ⇒
        JsonContent(Json.parse(payload))
      case _ ⇒
        OctetsContent(payload, mediaType)
    }
  }
}

/** Empty Content
  * Generates an empty enumerator of byte array with octet-stream media type
  */
case object EmptyContent extends Content[Unit] {
  def content: Unit = ()
  def mediaType: MediaType = MediaTypes.`application/octet-stream`
  def toEnumerator(implicit ec: ExecutionContext): Enumerator[Array[Byte]] = Enumerator.empty[Array[Byte]]
  def toBytes(implicit ec: ExecutionContext) = Future.successful { Array.empty[Byte] }
}

/** Content That Is Enumerated As An Array of Bytes
  * This is a degenerate form of content in that it is already digested as an Enumerator
  *
  * @param content The Enumerator the provides the content
  * @param mediaType The kind of media that is provided
  */
case class EnumeratedContent(
  content : Enumerator[Array[Byte]],
  mediaType : MediaType = MediaTypes.`application/octet-stream`
) extends Content[Enumerator[Array[Byte]]] {
  def toEnumerator(implicit ec: ExecutionContext) : Enumerator[Array[Byte]] = { content }
  def toBytes(implicit ec: ExecutionContext) : Future[Array[Byte]] = {
    content.run(Iteratee.consume[Array[Byte]]())
  }
}

/** Content That Is Enumerated by a linear sequence of enuemrators of byte arrays
  * This handles content that contents from combining a set of Enumerators together
  *
  * @param content The enumerators from which the content is formed
  * @param mediaType The kind of media that is provided
  */
case class EnumeratorsContent(
    content : Seq[Enumerator[Array[Byte]]],
    mediaType : MediaType = MediaTypes.`application/octet-stream`
) extends Content[Seq[Enumerator[Array[Byte]]]] {
  def toEnumerator(implicit ec: ExecutionContext) : Enumerator[Array[Byte]] = {
    content.foldLeft(Enumerator.empty[Array[Byte]]) { case (combined,next) ⇒ combined.andThen(next) }
  }
  def toBytes(implicit ec: ExecutionContext) : Future[Array[Byte]] = {
    toEnumerator(ec).run(Iteratee.consume[Array[Byte]]())
  }
}

/** Content with an InputStream.
  *
  * This kind of Content contains an InputStream for its payload that the client of this can use to read
  * data. This is often a more convenient content than EnumeratorContent.
  *
  * @param content The InputStream to be read
  * @param mediaType The ContentType of the InputStream
  */
case class StreamContent(
  content : InputStream,
  mediaType : MediaType = MediaTypes.`application/octet-stream`
) extends Content[InputStream] {
  def toEnumerator(implicit ec: ExecutionContext) = Enumerator.fromStream(content, 64 * 1024)
  def toBytes(implicit ec: ExecutionContext) : Future[Array[Byte]] = {
    toEnumerator(ec).run(Iteratee.consume[Array[Byte]]())
  }
}

/** Content with a Octet (Byte) Array.
  * This kind of Content contains an array of data that the client of the OctetsContent can use.
  *
  * @param content The data of the content
  * @param mediaType The ContentType of the data
  */
case class OctetsContent(
  content : Array[Byte],
  mediaType : MediaType = MediaTypes.`application/octet-stream`
) extends Content[Array[Byte]] {
  def toEnumerator(implicit ec: ExecutionContext) = Enumerator(content)
  def toBytes(implicit ec: ExecutionContext) = Future.successful { content }
}

/** Content with a simple text string.
  * This kind of content just encapsulates a String and defaults its ContentType to text/plain(UTF-8). That ContentType
  * should not be changed unless there is a significant need to as using UTF-8 as the base character encoding is
  * standard across Scrupal
  *
  * @param content The string content of the response
  */
case class TextContent(
  content : String
) extends Content[String] {
  val mediaType : MediaType = MediaTypes.`text/plain`
  def toEnumerator(implicit ec: ExecutionContext) = Enumerator(content.getBytes(utf8))
  def toBytes(implicit ec: ExecutionContext) = Future.successful { content.getBytes(utf8) }
}

/** Content with an Twirl Html payload.
  * This kind of content just encapsulates a Twirl Html object and defaults its ContentType to text/html.
  *
  * @param content The Html payload of the content.
  */
case class HtmlContent(
  content : Html
) extends Content[Html] {
  val mediaType : MediaType = MediaTypes.`text/html`
  def toEnumerator(implicit ec: ExecutionContext) = Enumerator(content.body.getBytes(utf8))
  def toBytes(implicit ec: ExecutionContext) = Future.successful { content.body.getBytes(utf8) }
}

/** Content with a BSONDocument payload.
  *
  * This kind of content just encapsulates a MongoDB BSONDocument content. Note that the ContentType is not modifiable
  * here as it is hard coded to ScrupalMediaTypes.bson. BSON is not a "standard" media type so we invent our own.
  *
  * @param content The JsValue payload of the content.
  */
case class JsonContent(
  content : JsValue
) extends Content[JsValue] {
  val mediaType : MediaType = MediaTypes.`application/json`
  def toEnumerator(implicit ec: ExecutionContext) = {
    Enumerator(Json.stringify(content).getBytes(utf8))
  }
  def toBytes(implicit ec: ExecutionContext) = Future.successful { Json.stringify(content).getBytes(utf8) }
}

/** Content with an Throwable payload.
  *
  * This kind of content just encapsulates an error embodied by a Throwable. This is how hard errors are returned from
  * a content. Note that the Disposition is always an Exception
  *
  * @param content The error that occurred
  */
case class ThrowableContent(
  content : Throwable,
  mediaType : MediaType = MediaTypes.`text/html`
) extends Content[Throwable] {
  def toHtml : Html = {
    scrupal.core.html.throwable(content)
  }
  def toTxt : Txt = {
    scrupal.core.txt.throwable(content)
  }
  def toJson : JsObject = {
    Json.obj(
      "class" -> content.getClass.getCanonicalName,
      "message" → content.getMessage,
      "stack" → ExceptionUtils.getStackTrace(content),
      "rootCauseMessage" → ExceptionUtils.getRootCauseMessage(content),
      "rootCauseStack" → ExceptionUtils.getRootCauseStackTrace(content).mkString("\n\tat ")
    )
  }
  def toEnumerator(implicit ec: ExecutionContext) = {
    mediaType match {
      case MediaTypes.`text/html` ⇒
        Enumerator(toHtml.body.getBytes(utf8))
      case MediaTypes.`text/plain` ⇒
        Enumerator(toTxt.body.getBytes(utf8))
      case MediaTypes.`application/json` ⇒
        Enumerator(Json.stringify(toJson).getBytes(utf8))
      case _ ⇒
        Enumerator(content.toString.getBytes(utf8))
    }
  }
  def toBytes(implicit ec: ExecutionContext) = Future.successful {
    mediaType match {
      case MediaTypes.`text/html` ⇒
        toHtml.body.getBytes(utf8)
      case MediaTypes.`text/plain` ⇒
        toTxt.body.getBytes(utf8)
      case MediaTypes.`application/json` ⇒
        Json.stringify(toJson).getBytes(utf8)
      case _ ⇒
        content.toString.getBytes(utf8)
    }
  }
}
