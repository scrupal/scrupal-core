package scrupal.core

import akka.http.scaladsl.model.MediaTypes
import play.api.http.HeaderNames
import play.api.test.FakeRequest
import scrupal.test.ScrupalSpecification

/** Title Of Thing.
  *
  * Description of thing
  */
class StimulusSpec extends ScrupalSpecification("Stimulus") {

  "Stimulus" should {
    val request = FakeRequest("GET", "/")
    val stim1 = Stimulus(context, request)
    val stim2 = Stimulus(context, request.withHeaders(HeaderNames.CONTENT_TYPE → "application/json"))
    "have a media type" in {
      stim1.mediaTyp must beEqualTo(MediaTypes.`application/octet-stream`)
      stim2.mediaTyp must beEqualTo(MediaTypes.`application/json`)
    }
    "have all the fields of a request" in {
      stim1.id must beEqualTo(666)
      stim1.tags.isEmpty must beTrue
      stim1.uri must beEqualTo("/")
      stim1.path must beEqualTo("/")
      stim1.method must beEqualTo("GET")
      stim1.version must beEqualTo("HTTP/1.1")
      stim1.queryString must beEqualTo(Map.empty[String,Seq[String]])
      stim1.headers.toSimpleMap.isEmpty must beTrue
      stim1.remoteAddress must beEqualTo("127.0.0.1")
      stim1.secure must beFalse
    }
  }
}
