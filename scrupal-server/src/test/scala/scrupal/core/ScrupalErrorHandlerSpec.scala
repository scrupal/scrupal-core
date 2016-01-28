package scrupal.core

import java.io.InterruptedIOException
import java.sql.SQLTimeoutException

import com.reactific.helpers.{ThrowingHelper, NotImplementedException}
import org.specs2.execute.AsResult
import org.specs2.execute.{Result⇒SpecsResult}
import play.api.http.Status
import play.api.mvc.{Result, RequestHeader}
import play.api.test.FakeRequest
import scrupal.test.{SharedTestScrupal, FakeSite, ScrupalSpecification}

import scala.concurrent.{Future, TimeoutException}

class ScrupalErrorHandlerSpec extends ScrupalSpecification("ScrupalErrorHandler") with SharedTestScrupal {

  def setup(name : String) : RequestHeader = {
    new FakeSite(SiteData(name, s"$name.com"))(scrupal)
    FakeRequest("GET", "/index.html").withHeaders("Host" -> s"$name.com:80")
  }

  def checkResult(result: Future[Result], status: Int) : SpecsResult = {
    result.isCompleted must beTrue
    result.value.isDefined must beTrue
    result.value.get.isSuccess must beTrue
    val res = result.value.get.get
    AsResult.apply(res.header.status must beEqualTo(status))
  }

  "ScrupalErrorHandler" should {
    "generate BadRequest errors" in {
      val request = setup("one")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onClientError(request, Status.BAD_REQUEST, "fake"), Status.BAD_REQUEST)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onClientError(request2, Status.BAD_REQUEST, "fake"), Status.BAD_REQUEST)
    }
    "generate Unauthorized errors" in {
      val request = setup("two")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onClientError(request, Status.UNAUTHORIZED, "fake"), Status.UNAUTHORIZED)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onClientError(request2, Status.UNAUTHORIZED, "fake"), Status.UNAUTHORIZED)
    }
    "generate Forbidden errors" in {
      val request = setup("three")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onClientError(request, Status.FORBIDDEN, "fake"), Status.FORBIDDEN)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onClientError(request2, Status.FORBIDDEN, "fake"), Status.FORBIDDEN)
    }
    "generate NotFound errors" in {
      val request = setup("four")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onClientError(request, Status.NOT_FOUND, "fake"), Status.NOT_FOUND)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onClientError(request2, Status.NOT_FOUND, "fake"), Status.NOT_FOUND)
    }
    "generate Other Client errors" in {
      val request = setup("eight")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onClientError(request, Status.METHOD_NOT_ALLOWED, "fake"), Status.METHOD_NOT_ALLOWED)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onClientError(request2, Status.METHOD_NOT_ALLOWED, "fake"), Status.METHOD_NOT_ALLOWED)
    }
    "handle NotImplementedError" in {
      val request = setup("five")
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(seh.onServerError(request, new NotImplementedError("fake")), Status.NOT_IMPLEMENTED)
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(seh.onServerError(request2, new NotImplementedError("fake")), Status.INTERNAL_SERVER_ERROR)
    }
    "handle NotImplementedException" in {
      val request = setup("six")
      val component = new ThrowingHelper {}
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(
        seh.onServerError(request, new NotImplementedException(component, "fake")), Status.NOT_IMPLEMENTED
      )
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(
        seh.onServerError(request2, new NotImplementedException(component, "fake")), Status.INTERNAL_SERVER_ERROR
      )
    }
    "handle Other exceptions" in {
      val request = setup("nine")
      val component = new ThrowingHelper {}
      val seh = new ScrupalErrorHandler(scrupal)
      checkResult(
        seh.onServerError(request, new IllegalArgumentException("fake: ignore me")), Status.INTERNAL_SERVER_ERROR
      )
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      checkResult(
        seh.onServerError(request2, new IllegalArgumentException("fake: ignore me")), Status.INTERNAL_SERVER_ERROR
      )
    }
    "handle service unavailable" in {
      val request = setup("seven")
      val seh = new ScrupalErrorHandler(scrupal)
      val timeouts = Seq(
        new TimeoutException("fake"), new InterruptedException(),
        new SQLTimeoutException(), new InterruptedIOException()
      )
      val request2 = FakeRequest("GET","/").withHeaders("Host" → s"oops.com:80")
      for (xcptn ← timeouts) {
        checkResult(seh.onServerError(request, xcptn), Status.SERVICE_UNAVAILABLE)
        checkResult(seh.onServerError(request2, xcptn), Status.SERVICE_UNAVAILABLE)
      }
      success
    }
    "handle exception while handling exception" in {
      val header = setup("ten")
      val seh = new ScrupalErrorHandler(scrupal) {
        override def defaultServerError(request : RequestHeader, x : Throwable) = {
          toss("exception in handling exception")
        }
      }
      val result = seh.onServerError(header, new Exception("foo"))
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.INTERNAL_SERVER_ERROR)
    }
  }
}
