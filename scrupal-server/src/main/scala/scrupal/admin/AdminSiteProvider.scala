package scrupal.admin

import play.api.libs.json._
import play.api.routing.sird._

import scrupal.core._

import scala.concurrent.Future

class AdminSiteProvider extends {
  val id = 'AdminSiteProvider
} with Provider with Enablee {

  def provide: ReactionRoutes = {
    case GET(p"/site/list") ⇒ Reactor.of {list}
    case GET(p"/site/${long(oid)}") ⇒ Reactor { site(oid) }
    case GET(p"/site/$byName") ⇒ Reactor { site(byName) }
    case POST(p"/site/") ⇒ Reactor.of { createSite }
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
    stimulus.context.scrupal.mapQuery( schema ⇒ schema.sites.byId(id)) {
      case Some(siteData) ⇒
        Response(site2json(siteData), Successful)
      case _ ⇒
        Response(JsNull, Unlocatable)
    }
  }

  def site(theName: String)(stimulus: Stimulus) : Future[RxResponse] = {
    stimulus.context.scrupal.mapQuery { schema ⇒ schema.sites.byName(theName) } {
      case siteData : Seq[SiteData] ⇒
        val sites = siteData.map { sd: SiteData ⇒ site2json(sd) }
        if (sites.nonEmpty)
          Response(JsArray(sites), Successful)
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
