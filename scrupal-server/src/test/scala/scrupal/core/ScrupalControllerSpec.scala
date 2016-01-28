package scrupal.core

import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.{Result, RequestHeader}
import play.api.test.FakeRequest
import scrupal.test.{SharedTestScrupal, ScrupalSpecification}

import scala.concurrent.Future

/** Test Cases For ScrupalController */
class ScrupalControllerSpec extends ScrupalSpecification("ScrupalController") with SharedTestScrupal {

  "ScrupalController" should {
    "yield NotImplemented for No context" in {
      case class TestController(scrupal: Scrupal, messagesApi: MessagesApi) extends ScrupalController {
        def pathPrefix: String = "foo"
        def reactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        def contextFor(thing: String, request: RequestHeader): Option[Context] = None
      }

      val ctrlr = TestController(scrupal, scrupal.messagesApi)
      val req = FakeRequest("GET", "/api/foo/bar")
      route(scrupal.application, req) match {
        case Some(fr) ⇒
          val result = await(fr)
          result.header.status must beEqualTo(Status.NOT_IMPLEMENTED)
        case None ⇒
          0 must beEqualTo(Status.NOT_IMPLEMENTED)
      }
    }

    "yield NotImplemented for No route" in {
      case class TestController(scrupal: Scrupal, messagesApi: MessagesApi) extends ScrupalController {
        def pathPrefix: String = "foo"
        def reactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = None
        def contextFor(thing: String, request: RequestHeader): Option[Context] = Some(Context(scrupal))
      }

      val ctrlr = TestController(scrupal, scrupal.messagesApi)
      val req = FakeRequest("GET", "/api/foo/bar")
      route(scrupal.application, req) match {
        case Some(fr) ⇒
          val result = await(fr)
          result.header.status must beEqualTo(Status.NOT_IMPLEMENTED)
        case None ⇒
          0 must beEqualTo(Status.NOT_IMPLEMENTED)
      }
    }
  }
}
