package scrupal.core

import play.api.test.FakeRequest
import scrupal.test.ControllerSpecification

class ApplicationControllerSpec extends ControllerSpecification("ApplicationController") {

  def testCases: Seq[Case] = Seq.empty[Case]

  "ApplicationController" should {

    "not provide route for nonsense" in withScrupal("nonsense_route_failure") { scrupal ⇒
      val context = Context(scrupal)
      val request = FakeRequest("GET", "/app/totally/fake/route")
      scrupal.appController.reactorFor(context, "nada", request) must beEqualTo(None)
    }
  }

}
