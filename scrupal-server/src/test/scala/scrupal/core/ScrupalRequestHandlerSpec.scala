package scrupal.core

import play.api.http.Status
import play.api.test.FakeRequest
import scrupal.test.{SharedTestScrupal, FakeSite, ScrupalSpecification}


class ScrupalRequestHandlerSpec extends ScrupalSpecification("ScrupalRequestHandler") with SharedTestScrupal {

  "ScrupalRequestHandler" should {
    "route request to correct site" in {
      val site1 = new FakeSite(SiteData("one", "one.com"))(scrupal)
      val site2 = new FakeSite(SiteData("two", "two.com"))(scrupal)
      val request = FakeRequest("GET", "/foo/index.html").withHeaders("Host" -> "one.com:80")
      route(scrupal.application, request) match {
        case Some(fr) ⇒
          val result = await(fr)
          result.header.status must beEqualTo(Status.NOT_IMPLEMENTED)
          success
        case None ⇒
          failure("unable to route")
      }
    }
  }
}
