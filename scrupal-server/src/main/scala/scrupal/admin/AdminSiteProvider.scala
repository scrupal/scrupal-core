package scrupal.admin

import play.api.libs.json._
import play.api.routing.sird._

import scrupal.core._

import scala.concurrent.Future

class AdminSiteProvider(val scrupal : Scrupal) extends {
  val id = 'AdminSiteProvider
} with Provider with Enablee with WithCoreSchema {

  def provide: ReactionRoutes = {
    case GET(p"/list") ⇒ Reactor.of {list}
    case GET(p"/${long(oid)}") ⇒ Reactor {site(oid)}
    case GET(p"/$byName") ⇒ Reactor { site(byName) }
    case POST(p"/") ⇒ Reactor.of {createSite}
  }

  /*  GET     /admin/site/$id<[0-9]+>         scrupal.admin.AdminController.site(id: Long)
        GET     /admin/site/                    scrupal.admin.AdminController.newSite()
        POST    /admin/site/create              scrupal.admin.AdminController.createSite()
        POST    /admin/site/update/$id<[0-9]+>  scrupal.admin.AdminController.updateSite(id: Long)
        GET     /admin/module/:id               scrupal.admin.AdminController.module(id: String)
        GET     /admin                          scrupal.admin.AdminController.index()
        GET     /admin/                         scrupal.admin.AdminController.index()
    */

  def list(stimulus: Stimulus): RxResponse = {
    val sites = stimulus.context.scrupal.sites.map {
      case (sym, site) ⇒
        sym -> site.data
    } map { case (sym, data) ⇒ Json.obj("name" → JsString(sym.name), "description" -> JsString(data.description)) }
    Response(JsArray(sites.toSeq), Successful)
  }

  private def site2json(sd: SiteData) : JsValue = {
    Json.obj(
      "name" → sd.name, "modified" → sd.modifiedAt(), "description" → sd.description,
      "domains" → sd.domainName, "created" → sd.createdAt(), "oid" → sd.oid,
      "requireHttps" → sd.requireHttps
    )
  }
  def site(id: Long)(stimulus: Stimulus): Future[RxResponse] = {
    mapQuery((schema) ⇒ schema.sites.byId(id)) {
      case (Some(siteData), ec) ⇒
        Response(site2json(siteData), Successful)
      case _ ⇒
        Response(JsNull, Unlocatable)
    }
  }

  def site(theName: String)(stimulus: Stimulus) : Future[RxResponse] = {
    mapQuery((schema) ⇒ schema.sites.byName(theName)) {
      case (siteData,ec) ⇒
        val sites = siteData.map { sd : SiteData ⇒ site2json(sd) }
        if (sites.nonEmpty)
          Response(JsArray(sites),Successful)
        else
          Response(JsArray(), Unlocatable)
      case _ ⇒
        Response(JsArray(), Unlocatable)
    }
  }

  def createSite(stimulus: Stimulus): RxResponse = {
    UnimplementedResponse("foo")
  }
}
