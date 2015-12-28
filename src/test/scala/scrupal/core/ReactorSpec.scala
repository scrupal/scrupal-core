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
    def apply(stim: Stimulus) : Future[Response] = {
      Future.successful(StringResponse(msg))
    }
    val description: String = "for testing"
    val oid: Option[Long] = None
  }

  "Reactor" should {
    "respond to Stimulus" in {
      val reactor = SimpleReactor("simple")
      val future = reactor(Stimulus.empty).map { resp =>
        resp.disposition must beEqualTo(Successful)
        resp.isInstanceOf[StringResponse] must beTrue
        resp.asInstanceOf[StringResponse].content must beEqualTo("simple")
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
      val ur = UnimplementedReactor("test", None)
      val future = ur.apply(Stimulus.empty).map { resp =>
        resp.disposition must beEqualTo(Unimplemented)
      }
      await(future)
    }
  }
}
