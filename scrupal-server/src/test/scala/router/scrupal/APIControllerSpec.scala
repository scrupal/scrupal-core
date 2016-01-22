package router.scrupal

import com.reactific.helpers.LoggingHelper
import org.specs2.execute.AsResult
import play.api.http.Status
import play.api.libs.iteratee.Iteratee
import play.api.test.FakeRequest
import scrupal.core._
import scrupal.test.ScrupalSpecification

import scala.concurrent.ExecutionContext.Implicits.global

/** Test Cases For EntityController */
class APIControllerSpec extends ScrupalSpecification("EntityController") {

  LoggingHelper.setToDebug(this)
  LoggingHelper.setToDebug(scrupal.apiController)

  case class TestEntityProvider(id : Symbol) extends EntityProvider

  class SiteForAPITest(siteName: String)(implicit scrpl: Scrupal)
      extends Site(new SiteData(siteName, domainName="foo.com"))(scrpl) {
    val tep1 = TestEntityProvider(Symbol("foot"))
    val tep2 = TestEntityProvider(Symbol("book"))
    enable(tep1,this)
    enable(tep2,this)
    require(delegates.toSeq.contains(tep1),"Failed to register tep1")
  }


  "APIController" should {
    "support all entity invocations" in {
      val site = new SiteForAPITest("testSite")
      val cases = Seq(
        ("GET","/api/feet/42/nada")           → "feet.retrieveById(id=42,details=nada)",
        ("GET","/api/feet/forty-two/nada")    → "feet.retrieveByName(name=forty-two,details=nada)",
        ("HEAD","/api/feet/42/nada")          → "feet.infoById(id=42,details=nada)",
        ("HEAD","/api/feet/forty-two/nada")   → "feet.infoByName(name=forty-two,details=nada)",
        ("OPTIONS","/api/feet/nada")          → "feet.query(details=nada)",
        ("POST","/api/feet/nada")             → "feet.create(details=nada)",
        ("PUT","/api/feet/42/nada")           → "feet.updateById(id=42,details=nada)",
        ("PUT","/api/feet/forty-two/nada")    → "feet.updateByName(name=forty-two,details=nada)",
        ("DELETE","/api/feet/42/nada")        → "feet.deleteById(id=42,details=nada)",
        ("DELETE","/api/feet/forty-two/nada") → "feet.deleteByName(name=forty-two,details=nada)",
        ("GET","/api/books/42/nada")           → "books.retrieveById(id=42,details=nada)",
        ("GET","/api/books/forty-two/nada")    → "books.retrieveByName(name=forty-two,details=nada)",
        ("HEAD","/api/books/42/nada")          → "books.infoById(id=42,details=nada)",
        ("HEAD","/api/books/forty-two/nada")   → "books.infoByName(name=forty-two,details=nada)",
        ("OPTIONS","/api/books/nada")          → "books.query(details=nada)",
        ("POST","/api/books/nada")             → "books.create(details=nada)",
        ("PUT","/api/books/42/nada")           → "books.updateById(id=42,details=nada)",
        ("PUT","/api/books/forty-two/nada")    → "books.updateByName(name=forty-two,details=nada)",
        ("DELETE","/api/books/42/nada")        → "books.deleteById(id=42,details=nada)",
        ("DELETE","/api/books/forty-two/nada") → "books.deleteByName(name=forty-two,details=nada)",
        ("GET","/api/foot/42/facet/facet_id/nada") →
          "foot.getById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("GET","/api/foot/forty-two/facet/facet_id/nada") →
          "foot.getByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("HEAD","/api/foot/42/facet/facet_id/nada") →
          "foot.facetInfoById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("HEAD","/api/foot/forty-two/facet/facet_id/nada") →
          "foot.facetInfoByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("OPTIONS","/api/foot/42/facet/nada") →
          "foot.findById(id=42,facet=facet,details=nada)",
        ("OPTIONS","/api/foot/forty-two/facet/nada") →
          "foot.findByName(name=forty-two,facet=facet,details=nada)",
        ("POST","/api/foot/42/facet/nada") →
          "foot.addById(id=42,facet=facet,details=nada)",
        ("POST","/api/foot/forty-two/facet/nada") →
          "foot.addByName(name=forty-two,facet=facet,details=nada)",
        ("PUT","/api/foot/42/facet/facet_id/nada") →
          "foot.setById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("PUT","/api/foot/forty-two/facet/facet_id/nada") →
          "foot.setByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("DELETE","/api/foot/42/facet/facet_id/nada") →
          "foot.removeById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("DELETE","/api/foot/forty-two/facet/facet_id/nada") →
          "foot.removeByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("GET","/api/book/42/facet/facet_id/nada") →
          "book.getById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("GET","/api/book/forty-two/facet/facet_id/nada") →
          "book.getByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("HEAD","/api/book/42/facet/facet_id/nada") →
          "book.facetInfoById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("HEAD","/api/book/forty-two/facet/facet_id/nada") →
          "book.facetInfoByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("OPTIONS","/api/book/42/facet/nada") →
          "book.findById(id=42,facet=facet,details=nada)",
        ("OPTIONS","/api/book/forty-two/facet/nada") →
          "book.findByName(name=forty-two,facet=facet,details=nada)",
        ("POST","/api/book/42/facet/nada") →
          "book.addById(id=42,facet=facet,details=nada)",
        ("POST","/api/book/forty-two/facet/nada") →
          "book.addByName(name=forty-two,facet=facet,details=nada)",
        ("PUT","/api/book/42/facet/facet_id/nada") →
          "book.setById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("PUT","/api/book/forty-two/facet/facet_id/nada") →
          "book.setByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)",
        ("DELETE","/api/book/42/facet/facet_id/nada") →
          "book.removeById(id=42,facet=facet,facet_id=facet_id,details=nada)",
        ("DELETE","/api/book/forty-two/facet/facet_id/nada") →
          "book.removeByName(name=forty-two,facet=facet,facet_id=facet_id,details=nada)"

      )
      for (((method,path),expected) ← cases) {
        val req = FakeRequest(method, path).withHeaders("Host"→"foo.com")
        route(scrupal.application, req) match {
          case Some(fr) ⇒
            val result = await(fr)
            val future = result.body.run(Iteratee.consume[Array[Byte]]().map(x => new String(x, utf8))).map { body ⇒
              if (result.header.status != Status.NOT_IMPLEMENTED) {
                failure(s"Wrong status (${result.header.status}) for request ($req) with result body ($body)")
              }
              body must beEqualTo(s"Unimplemented: $expected")
            }
            await(future)
          case None ⇒
            failure("route not found")
        }
      }
      success
    }
  }
}
