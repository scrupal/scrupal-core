package scrupal.core

import akka.http.scaladsl.model.MediaTypes
import org.specs2.execute.AsResult
import play.api.libs.json.{JsString, JsObject, Json}
import play.api.test.FakeRequest
import scrupal.html.ValidatingSpecification
import scrupal.test.SharedTestScrupal

import scala.concurrent.ExecutionContext

class ApplicationProviderSpec extends ValidatingSpecification("ApplicationProvider") with SharedTestScrupal {

  implicit val ec : ExecutionContext = scrupal.executionContext

  "ApplicationProvider" should {
    case class TestApplicationProvider(id : Identifier)(implicit val scrupal : Scrupal) extends ApplicationProvider
    "should load the application" in {
      val tap = TestApplicationProvider('foo)
      val results = for (url ← Seq("/foo", "/foo/")) yield {
        val req = FakeRequest("GET", url).withHeaders("Host" → "foo.com:80")
        tap.reactorFor(req) match {
          case Some(reactor) ⇒
            val response = await(reactor(scrupal.stimulusForRequest(req)))
            response.disposition must beEqualTo(Successful)
            response.payload.mediaType must beEqualTo(MediaTypes.`text/html`)
            val future = response.payload.toBytes.map { bytes ⇒
              val page = new String(bytes,utf8)
              page must contain("foo")
              validate("LoadedApplication", page)
            }
            AsResult(await(future))
          case None ⇒
            failure("Should have found a reactor")
        }
      }
      results.count { r ⇒ r.isSuccess } must beEqualTo(results.size)
    }

    "should provide application info" in {
      val tap = TestApplicationProvider('info)
      val req = FakeRequest("GET", "/info/info").withHeaders("Host" → "foo.com:80")
      tap.reactorFor(req) match {
        case Some(reactor) ⇒
          val response = await(reactor(scrupal.stimulusForRequest(req)))
          response.disposition must beEqualTo(Successful)
          response.payload.mediaType must beEqualTo(MediaTypes.`application/json`)
          val future = response.payload.toBytes.map { bytes ⇒
            val json = new String(bytes,utf8)
            val js = Json.parse(json)
            js.isInstanceOf[JsObject] must beTrue
            val lookup = js.asInstanceOf[JsObject] \ "name"
            val name = lookup.getOrElse(JsString("")).asInstanceOf[JsString]
            name.value must beEqualTo("info")
          }
          AsResult(await(future))
        case None ⇒
          failure("should have found a reactor")
      }
    }
  }
}
