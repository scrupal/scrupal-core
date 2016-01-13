package scrupal.core

import java.io.InterruptedIOException
import java.sql.SQLTimeoutException

import com.reactific.helpers.{ThrowingHelper, NotImplementedException}
import play.api.http.Status
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.test.{FakeSite, ScrupalSpecification}

import scala.concurrent.TimeoutException

class ScrupalErrorHandlerSpec extends ScrupalSpecification("ScrupalErrorHandler") {

  def setup(name : String) : RequestHeader = {
    new FakeSite(SiteData(name, s"$name.com"))(scrupal)
    FakeRequest("GET", "/index.html").withHeaders("Host" -> s"$name.com:80")
  }

  "ScrupalErrorHandler" should {
    "generate BadRequest errors" in {
      val header = setup("one")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onClientError(header, Status.BAD_REQUEST, "fake")
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.BAD_REQUEST)
    }
    "generate Unauthorized errors" in {
      val header = setup("two")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onClientError(header, Status.UNAUTHORIZED, "fake")
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.UNAUTHORIZED)
    }
    "generate Forbidden errors" in {
      val header = setup("three")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onClientError(header, Status.FORBIDDEN, "fake")
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.FORBIDDEN)
    }
    "generate NotFound errors" in {
      val header = setup("four")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onClientError(header, Status.NOT_FOUND, "fake")
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.NOT_FOUND)
    }
    "generate Other Client errors" in {
      val header = setup("eight")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onClientError(header, Status.METHOD_NOT_ALLOWED, "fake")
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.METHOD_NOT_ALLOWED)
    }
    "handle NotImplementedError" in {
      val header = setup("five")
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onServerError(header, new NotImplementedError("fake"))
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.NOT_IMPLEMENTED)
    }
    "handle NotImplementedException" in {
      val header = setup("six")
      val component = new ThrowingHelper {}
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onServerError(header, new NotImplementedException(component, "fake"))
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.NOT_IMPLEMENTED)
    }
    "handle Other exceptions" in {
      val header = setup("nine")
      val component = new ThrowingHelper {}
      val seh = new ScrupalErrorHandler(scrupal)
      val result = seh.onServerError(header, new IllegalArgumentException("fake: ignore me"))
      result.isCompleted must beTrue
      result.value.isDefined must beTrue
      result.value.get.isSuccess must beTrue
      val res = result.value.get.get
      res.header.status must beEqualTo(Status.INTERNAL_SERVER_ERROR)
    }
    "handle timeouts" in {
      val header = setup("seven")
      val seh = new ScrupalErrorHandler(scrupal)
      val timeouts = Seq(
        new TimeoutException("fake"), new InterruptedException(),
        new SQLTimeoutException(), new InterruptedIOException()
      )
      for (xcptn ← timeouts) {
        val result = seh.onServerError(header, xcptn)
        result.isCompleted must beTrue
        result.value.isDefined must beTrue
        result.value.get.isSuccess must beTrue
        val res = result.value.get.get
        res.header.status must beEqualTo(Status.SERVICE_UNAVAILABLE)
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
