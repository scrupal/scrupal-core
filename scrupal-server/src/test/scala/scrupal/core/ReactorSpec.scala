package scrupal.core

import play.api.http.Status
import play.api.test.FakeRequest
import scrupal.test.ScrupalSpecification

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/** Title Of Thing.
  *
  * Description of thing
  */
class ReactorSpec extends ScrupalSpecification("Reactor") {

  case class SimpleReactor(msg : String) extends Reactor {
    def apply(stim: Stimulus) : Future[RxResponse] = {
      Future.successful(Response(msg,Successful))
    }
    val description: String = "for testing"
    val oid: Option[Long] = None
  }

  "Reactor" should {
    "respond to Stimulus" in {
      val reactor = SimpleReactor("simple")
      val future = reactor(Stimulus.empty).map { resp : RxResponse =>
        resp.payload.isInstanceOf[TextContent] must beTrue
        resp.disposition must beEqualTo(Successful)
        val textResponse = resp.asInstanceOf[Response[TextContent]]
        textResponse.payload.content must beEqualTo("simple")
      }
      await(future)
    }
    "respond to Context/Request pair" in {
      val reactor = SimpleReactor("simple")
      val future = reactor.resultFrom(SimpleContext(scrupal), FakeRequest("GET", "/")).map { result =>
        result.header.status must beEqualTo(Status.OK)
      }
      await(future)
    }
  }

  "UnimplementedReactor" should {
    "generated UnimplementedResponse" in {
      val ur = UnimplementedReactor("test")
      val future = ur.apply(Stimulus.empty).map { resp =>
        resp.disposition must beEqualTo(Unimplemented)
      }
      await(future)
    }
  }
}
