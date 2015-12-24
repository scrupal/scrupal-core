package scrupal.core

import play.api.http.{NoHttpFilters, HttpConfiguration}
import play.api.mvc.RequestHeader
import play.api.routing.Router
import play.api.test.FakeRequest
import scrupal.test.{FakeSite, ScrupalSpecification}

class ScrupalRequestHandlerSpec extends ScrupalSpecification("ScrupalRequestHandler") {

  "ScrupalRequestHandler" should {
    "route request to correct site" in {
      val site1 = new FakeSite("one", "one.com")(scrupal)
      val site2 = new FakeSite("two", "two.com")(scrupal)
      val app = scrupal.application
      val httpConf = HttpConfiguration.fromConfiguration(app.configuration)
      val srh = new ScrupalRequestHandler(scrupal, Router.empty, app.errorHandler, httpConf, NoHttpFilters)
      val header : RequestHeader = FakeRequest("GET", "/index.html").withHeaders("Host" -> "one.com:80")
      val (request, handler) = srh.handlerForRequest(header)
      handler.isInstanceOf[ReactorAction] must beTrue
      val ra = handler.asInstanceOf[ReactorAction]
      ra.context.site must beEqualTo(Some(site1))
    }
  }
}
