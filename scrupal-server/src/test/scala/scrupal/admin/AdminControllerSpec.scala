package scrupal.admin

import play.api.http.Status
import play.api.libs.iteratee.Iteratee
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.core._
import scrupal.test.ScrupalSpecification

import scala.concurrent.ExecutionContext.Implicits.global

/** Test Cases For AdminController */
class AdminControllerSpec extends ScrupalSpecification("AdminController") {

  class SiteForAdminControllerTest(siteName: String)(implicit scrpl: Scrupal)
    extends Site(new SiteData(siteName, domainName="foo.com"))(scrpl) {
    override def reactorFor(request: RequestHeader) : Option[Reactor] = {
      val reactor = super.reactorFor(request)
      reactor
    }
  }

  "AdminController" should {
    "yield None for irrelevant path" in {
      val req = FakeRequest("GET", "/api/foo/bar")
      scrupal.adminController.reactorFor(context, "foo", req) must beEqualTo(None)
    }

    "support all Admin URL Paths" in withH2CoreSchema("AdminPaths") { schema : CoreSchema_H2 ⇒
      val cases = Seq(
        ("GET","/admin/module/")        → "foo",
        ("GET","/admin/scrupal/")       → "<p>help</p>",
        ("GET","/admin/scrupal/help")   → "<p>help</p>",
        ("GET", "/admin/site/list")     → "",
        ("GET", "/admin/site/1")        → "",
        ("GET", "/admin/site/testSite") → "",
        ("POST","/admin/site/")         → "foo",
        ("GET", "/admin/user/")         → "foo"
      )
      val site = new SiteForAdminControllerTest("testSite")(scrupal)
      val f = schema.create().flatMap { u : Unit ⇒ schema.db.run { schema.sites.create(site.data) } }
      await(f) must beEqualTo(1L)
      for (((method,path),expected) ← cases) {
        val req = FakeRequest(method, path).withHeaders("Host"→"foo.com")
        route(scrupal.application, req) match {
          case Some(fr) ⇒
            val result = await(fr)
            val future = result.body.run(Iteratee.consume[Array[Byte]]().map(x => new String(x, utf8))).map { body ⇒
              if (result.header.status != Status.OK && result.header.status != Status.NOT_IMPLEMENTED) {
                failure(s"Wrong status (${result.header.status}) for request ($req) with result body ($body)")
              }
              body must contain(expected)
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
