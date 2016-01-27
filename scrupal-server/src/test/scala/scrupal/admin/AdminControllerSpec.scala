package scrupal.admin

import play.api.http.Status
import play.api.libs.iteratee.Iteratee
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.core._
import scrupal.test.{SharedTestScrupal, ScrupalSpecification}

import scala.concurrent.ExecutionContext.Implicits.global

/** Test Cases For AdminController */
class AdminControllerSpec extends ScrupalSpecification("AdminController") with SharedTestScrupal {

  class SiteForAdminControllerTest(siteName: String)(implicit scrpl: Scrupal)
    extends Site(new SiteData(siteName, domainName="foo.com"))(scrpl) {
    override def reactorFor(request: RequestHeader) : Option[Reactor] = {
      val reactor = super.reactorFor(request)
      reactor
    }
  }

  "AdminController" should {
    "yield None for irrelevant path" in withScrupal("IrrelevantPath") { (scrupal) ⇒
      val req = FakeRequest("GET", "/api/foo/bar")
      val context = Context(scrupal)
      scrupal.adminController.reactorFor(context, "foo", req) must beEqualTo(None)
    }

    "support all Admin URL Paths" in withScrupalSchema("AdminPaths") { (scrupal, schema) ⇒
      val cases = Seq(
        ("GET","/admin/module/")        → "foo",
        ("GET","/admin/scrupal/")       → "<p>help</p>",
        ("GET","/admin/scrupal/help")   → "<p>help</p>",
        ("POST","/admin/site/")         → "foo",
        ("GET", "/admin/user/")         → "foo",
        ("GET", "/admin/site/list")     → "",
        ("GET", "/admin/site/1")        → "",
        ("GET", "/admin/site/testSite") → ""
      )
      await(schema.create())
      await(
        schema.db.run {
          val site = new SiteForAdminControllerTest("testSite")(scrupal)
          schema.sites.create(site.data)
        }
      )

      for (((method,path),expected) ← cases) {
        val req = FakeRequest(method, path).withHeaders("Host"→"foo.com")
        route(scrupal.application, req) match {
          case Some(fr) ⇒
            val future = fr.flatMap { result ⇒
              result.body.run(Iteratee.consume[Array[Byte]]().map(x => new String(x, utf8))).map { body ⇒
                if (result.header.status != Status.OK && result.header.status != Status.NOT_IMPLEMENTED) {
                  failure(s"Wrong status (${result.header.status}) for request ($req) with result body ($body)")
                }
                body must contain(expected)
              }
            }
            await(future)
          case None ⇒
            failure("route not found")
        }
      }
      success
    }

    "have an index page" in {
      pending

      /*
        def index() = Action.async { request : Request[AnyContent] ⇒
          makePage(Ok, Administration.introduction)
        }
      */
    }

    "accept Json Site creation data" in {
      pending
      /*
      def postSiteJson() = Action.async { request : Request[AnyContent] ⇒
        request.body.asJson match {
          case Some(json) ⇒
            makePage(NotImplemented, p("JSON Creation Of Sites Not Yet Supported"))
          case None ⇒
            makePage(BadRequest, p("Expected JSON content"))
        }
      }
      */
    }

    "have a site creation form" in {
      pending
      /*


    val createSiteForm = Form[CreateSite](
      mapping(
        "name" → nonEmptyText(maxLength=64),
        "description" → nonEmptyText(maxLength=255),
        "domainName" → nonEmptyText(maxLength=255),
        "requireHttps" → boolean
      )(CreateSite.apply)(CreateSite.unapply)
    )

    def createSite() = Action.async { implicit request : Request[AnyContent] ⇒
      createSiteForm.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          makePage(BadRequest, Administration.site_form(formWithErrors))
        },
        data => {
          /* binding success, you get the actual value. */
          val siteData = SiteData(data.name, data.domainName,data.description,data.requireHttps)
          mapQuery( (schema) ⇒ schema.sites.create(siteData) ) { (id : Long, ec: ExecutionContext) ⇒
            val site = Site(siteData.copy(oid=Some(id)))(scrupal)
            Redirect(router.scrupal.core.routes.AdminController.site(id))
          }
        }
      )
    }

       */
    }

    "allow site updating" in {
      pending
      /*
      def updateSite(id : Long) = Action.async { implicit request : Request[AnyContent] ⇒
        flatMapQuery((schema) ⇒ schema.sites.byId(id)) {
          case (Some(siteData),ec) ⇒
            val boundForm = createSiteForm.bindFromRequest
            boundForm.fold(
              formWithErrors ⇒ {
                val page = Administration.site(siteData)(div(cls:="bg-warning","Save failed."))
                makePage(BadRequest, page)
              },
              data ⇒ {
                val newSiteData : SiteData = siteData.copy(name=data.name, description=data.description, domainName=data.domainName, requireHttps=data.requireHttps)
                flatMapQuery((schema) ⇒ schema.sites.update(newSiteData)) { (r,ec) ⇒
                  makePage(Ok, Administration.site(siteData)(div(cls:="bg-success","Saved.")))
                }
              }
            )
          case (None,ec) ⇒
            makePage(NotFound, Administration.error("Site #$id was not found."))
        }
      }
*/
    }

    "display a site" in {
      pending
      /*
      def newSite() = Action.async { implicit request : Request[AnyContent] ⇒
        makePage(Ok, Administration.site_form(createSiteForm))
      }

      def site(id : Long) = Action.async { implicit request : Request[AnyContent] ⇒
        flatMapQuery((schema) ⇒ schema.sites.byId(id)) {
          case (Some(siteData),ec) ⇒
            makePage(Ok, Administration.site(siteData)())
          case (None,ec) ⇒
            makePage(NotFound, Administration.error(s"Site #$id was not found."))
        }
      }
      */
    }
    "display a module" in {
      pending
      /*
      def module(id: String) = Action.async { implicit request : Request[AnyContent] ⇒
        makePage(Ok, Administration.module())
      }
       */
    }  }
}
