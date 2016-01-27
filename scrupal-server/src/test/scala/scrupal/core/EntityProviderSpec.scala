package scrupal.core

import akka.http.scaladsl.model.MediaTypes
import com.reactific.slickery.Storable._
import org.specs2.matcher.MatchResult
import play.api.test.FakeRequest
import scrupal.test.{SharedTestScrupal, ScrupalSpecification}

/** Test Cases For EntityProvider */
class EntityProviderSpec extends ScrupalSpecification("EntityProvider") with SharedTestScrupal {

  case class TestEntityProvider() extends { val id : Symbol = 'test } with EntityProvider

  def doEntityTest(method: String, path: String, expected: String) : MatchResult[Boolean] = {
    val tep = TestEntityProvider()
    val req = FakeRequest(method, path)
    tep.reactorFor(req) match {
      case Some(reactor) ⇒
        val stimulus = Stimulus(context, req)
        val future = reactor(stimulus)
        val response = await(future)
        response.disposition must beEqualTo(Unimplemented)
        response.mediaType must beEqualTo(MediaTypes.`text/plain`)
        val content = response.payload.asInstanceOf[TextContent]
        content.content.contains(expected) must beTrue
      case _ ⇒
        "reactor not found".nonEmpty must beFalse
    }
  }

  "EntityProvider" should {
    "create an entity" in {
      doEntityTest("POST", "/tests/details", "tests.create(details=details)")
    }
    "query an entity" in {
      doEntityTest("OPTIONS", "/tests/details", "tests.query(details=details)")
    }
    "retrieve an entity by id" in {
      doEntityTest("GET", "/tests/42/details", "tests.retrieveById(id=42,details=details)")
    }
    "retrieve an entity by name" in {
      doEntityTest("GET", "/tests/entNam/details", "tests.retrieveByName(name=entNam,details=details)")
    }
    "info an entity by id" in {
      doEntityTest("HEAD", "/tests/42/details", "tests.infoById(id=42,details=details)")
    }
    "info an entity by name" in {
      doEntityTest("HEAD", "/tests/entNam/details", "tests.infoByName(name=entNam,details=details)")
    }
    "update an entity by id" in {
      doEntityTest("PUT", "/tests/42/details", "tests.updateById(id=42,details=details)")
    }
    "update an entity by name" in {
      doEntityTest("PUT", "/tests/entNam/details", "tests.updateByName(name=entNam,details=details)")
    }
    "delete an entity by id" in {
      doEntityTest("DELETE", "/tests/42/details", "tests.deleteById(id=42,details=details)")
    }
    "delete an entity by name" in {
      doEntityTest("DELETE", "/tests/entNam/details", "tests.deleteByName(name=entNam,details=details)")
    }
    "add an entity facet by id" in {
      doEntityTest("POST", "/test/42/foo/details", "test.addById(id=42,facet=foo,details=details)")
    }
    "add an entity facet by name" in {
      doEntityTest("POST", "/test/entNam/foo/details", "test.addByName(name=entNam,facet=foo,details=details)")
    }
    "get an entity facet by id" in {
      doEntityTest("GET", "/test/42/foo/84/details", "test.getById(id=42,facet=foo,facet_id=84,details=details)")
    }
    "get an entity facet by name" in {
      doEntityTest("GET", "/test/entNam/foo/84/details",
        "test.getByName(name=entNam,facet=foo,facet_id=84,details=details"
      )
    }
    "info an entity facet by id" in {
      doEntityTest("HEAD", "/test/42/foo/84/details",
        "test.facetInfoById(id=42,facet=foo,facet_id=84,details=details)"
      )
    }
    "info an entity facet by name" in {
      doEntityTest("HEAD", "/test/entNam/foo/84/details",
        "test.facetInfoByName(name=entNam,facet=foo,facet_id=84,details=details")
    }
    "set an entity facet by id" in {
      doEntityTest("PUT", "/test/42/foo/84/details", "test.setById(id=42,facet=foo,facet_id=84,details=details)")
    }
    "set an entity facet by name" in {
      doEntityTest("PUT", "/test/entNam/foo/84/details",
        "test.setByName(name=entNam,facet=foo,facet_id=84,details=details")
    }
    "remove an entity facet by id" in {
      doEntityTest("DELETE", "/test/42/foo/84/details",
        "test.removeById(id=42,facet=foo,facet_id=84,details=details)")
    }
    "remove an entity facet by name" in {
      doEntityTest("DELETE", "/test/entNam/foo/84/details",
        "test.removeByName(name=entNam,facet=foo,facet_id=84,details=details")
    }
    "find an entity facet by id" in {
      doEntityTest("OPTIONS", "/test/42/foo/details", "test.findById(id=42,facet=foo,details=details)")
    }
    "find an entity facet by name" in {
      doEntityTest("OPTIONS", "/test/entNam/foo/details", "test.findByName(name=entNam,facet=foo,details=details)")
    }
  }
}
