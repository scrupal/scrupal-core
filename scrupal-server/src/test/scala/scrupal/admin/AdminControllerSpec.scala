package scrupal.admin

import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.core._
import scrupal.test.{ControllerSpecification, SharedTestScrupal}


/** Test Cases For AdminController */
class AdminControllerSpec extends ControllerSpecification("AdminController") with SharedTestScrupal {

  class SiteForAdminControllerTest(siteName: String)(implicit scrpl: Scrupal)
    extends Site(new SiteData(siteName, domainName="foo.com"))(scrpl) {
    override def reactorFor(request: RequestHeader) : Option[Reactor] = {
      val reactor = super.reactorFor(request)
      reactor
    }
  }

  def testCases : Seq[Case] = Seq(
    Case("GET","/admin/module/", Successful, "foo"),
    Case("GET","/admin/scrupal/",Successful, "<p>help</p>"),
    Case("GET","/admin/scrupal/help", Successful, "<p>help</p>"),
    Case("POST","/admin/site/", Unimplemented, "foo"),
    Case("GET", "/admin/user/", Successful, "foo"),
    Case("GET", "/admin/site/list", Successful, ""),
    Case("GET", "/admin/site/1", Successful, ""),
    Case("GET", "/admin/site/Foo", Successful, ""),
    Case("GET", "/crapola", Unlocatable, "")
  )

  "AdminController" should {
    "yield None for irrelevant path" in withScrupal("IrrelevantPath") { (scrupal) ⇒
      val req = FakeRequest("GET", "/api/foo/bar")
      val context = Context(scrupal)
      scrupal.adminController.reactorFor(context, "foo", req) must beEqualTo(None)
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
