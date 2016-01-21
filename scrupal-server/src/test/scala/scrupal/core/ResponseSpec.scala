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

import java.io.ByteArrayInputStream

import akka.http.scaladsl.model.MediaTypes
import org.specs2.matcher.MatchResult
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json.Json
import scrupal.test.ScrupalSpecification

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

import scalatags.Text.all._

class ResponseSpec extends ScrupalSpecification("Response") {

  scrupal.withExecutionContext { implicit ec : ExecutionContext  ⇒

    "Response" should {
      "have payload, mediaType and disposition" in {
        val response = Response(Array.empty[Byte], MediaTypes.`application/octet-stream`, Unimplemented)
        response.payload.content.isEmpty must beTrue
        response.mediaType must beEqualTo(MediaTypes.`application/octet-stream`)
        response.disposition must beEqualTo(Unimplemented)
      }
      "generate ExceptionResponse for exceptions thrown in Response.safely" in {
        val good = Response.safely { NoopResponse }
        val bad = Response.safely { throw new Exception("oops") }
        good.payload must beEqualTo(EmptyContent)
        bad.isInstanceOf[Response[_]] must beTrue
        val payload = bad.asInstanceOf[Response[_]].payload.asInstanceOf[ThrowableContent].content
        payload.getMessage must beEqualTo("oops")
      }
    }
    "NoopResponse" should {
      "have Received disposition" in {
        NoopResponse.disposition must beEqualTo(Received)
      }
      "have application/octet-stream media type" in {
        NoopResponse.mediaType must beEqualTo(MediaTypes.`application/octet-stream`)
      }
      "generate empty body" in {
        checkEnum(NoopResponse.toEnumerator, 0)
      }
      "convert to EnumeratorResponse" in {
        val er = NoopResponse.toEnumeratedResponse
        checkEnum(er.toEnumerator, 0)
      }
    }
    "EnumeratorResponse" should {
      "have reflective content" in {
        val er = Response(Enumerator.empty[Array[Byte]], MediaTypes.`text/plain`, Successful)
        er.toEnumerator must beEqualTo(er.payload.toEnumerator)
      }
    }

    def checkEnum(enum: Enumerator[Array[Byte]], sum: Int) : MatchResult[Int] = {
      val iteratee = Iteratee.fold[Array[Byte],Int](0) {
        case (total, elem) ⇒ total + elem.foldLeft[Int](0) { case (t, e) ⇒ t + e }
      }
      val future = (enum run iteratee).map { value ⇒ value must beEqualTo(sum) }
      Await.result(future,1.seconds)
    }

    "EnumeratorsResponse" should {
      "aggregate enumerators" in {
        val enums = Seq(Enumerator(Array[Byte](3,4)),Enumerator(Array[Byte](1,2)))
        val er = Response(enums, MediaTypes.`application/octet-stream`, Successful)
        val expected = Enumerator(Array[Byte](3,4,1,2))
        checkEnum(er.toEnumerator, 10)
      }
    }

    "StreamResponse" should {
      "read a stream" in {
        val stream = new ByteArrayInputStream(Array[Byte](0,1,2,3,4))
        val sr = Response(stream, MediaTypes.`application/octet-stream`, Successful)
        checkEnum(sr.toEnumerator, 10)
      }
    }

    "OctetsResponse" should {
      "read octest" in {
        val octets = Array[Byte](1,2,3,4,5)
        val or = Response(octets, MediaTypes.`application/octet-stream`, Successful)
        checkEnum(or.toEnumerator, 15)
      }
    }

    def strSum(str : String) : Int = str.foldLeft[Int](0) { case (sum,ch) ⇒ sum + ch.toInt }

    "StringResponse" should {
      "read string" in {
        val str = "This is a string of no significance."
        val sr = Response(str, Successful)
        checkEnum(sr.toEnumerator, strSum(str))
      }
    }

    "HtmlResponse" should {
      "read HTML" in {
        val content = html(body())
        val sr = Response(content, Successful)
        checkEnum(sr.toEnumerator, strSum(content.render))
      }
    }

    "JsonResponse" should {
      "read JSON" in {
        val json = Json.parse("""{ "foo" : 3 }""")
        val js_as_string = json.toString()
        val jr = Response(json, Successful)
        checkEnum(jr.toEnumerator, strSum(js_as_string))
      }
    }

    "ExceptionResponse" should {
      "handle an Exception" in {
        val exception = new Exception("fake")
        val er = Response(exception)
        val body = er.payload.toHtml.render
        body.contains("fake")
        checkEnum(er.toEnumerator, strSum(body))
      }
    }

    "ErrorResponse" should {
      "read an error" in {
        val error = "This is a string of no significance."
        val er = Response(error, Unspecified)
        val error_str = er.payload.content
        checkEnum(er.toEnumerator, strSum(error_str))
      }
    }

    "UnimplementedResponse" should {
      "generate unimplemented message" in {
        val what = "Not Done Yet"
        val ur = UnimplementedResponse(what)
        val expected = ur.formatted
        checkEnum(ur.toEnumerator, strSum(expected))
      }

      "have plain/text media type" in {
        val ur = UnimplementedResponse("something")
        ur.mediaType must beEqualTo(MediaTypes.`text/plain`)
      }
    }

  }

}
