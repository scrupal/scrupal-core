package router.scrupal.core

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import scrupal.core._
import scrupal.utils.ScrupalComponent

import scala.concurrent.Future

/** Title Of Thing.
  *
  * Description of thing
  */
class APIController(val scrupal : Scrupal, val messagesApi : MessagesApi) extends ScrupalController {

  def scrupalInfo(what : String) = Action {
    what.toLowerCase match {
      case "" ⇒ help()
      case "/" ⇒ help()
      case "help" ⇒ help()
      case _ ⇒ help()
    }
  }

  def help() : Result = {
    Ok("help")
  }

  def entityGET(entityName: String, rest : String) = entityAction(entityName, rest)
  def entityPUT(entityName: String, rest : String) = entityAction(entityName, rest)
  def entityPOST(entityName: String, rest : String) = entityAction(entityName, rest)
  def entityOPTIONS(entityName: String, rest : String) = entityAction(entityName, rest)
  def entityDELETE(entityName: String, rest : String) = entityAction(entityName, rest)
  def entityHEAD(entityName: String, rest : String) = entityAction(entityName, rest)

  protected def entityAction(entityName: String, rest: String) = {
    Action.async { implicit request : Request[AnyContent] ⇒
      val prefix = s"/api/"
      if (request.path.startsWith(prefix)) {
        val rh : RequestHeader = request.copy(path=request.path.toLowerCase.drop(prefix.length-1))
        log.debug(s"req2(method=${rh.method}, host=${rh.host}, uri=${rh.uri}, path=${rh.path}, remoteAddress=${rh.remoteAddress}")
        scrupal.siteForRequest(rh) match {
          case (Some(site), Some(subdomain)) ⇒
            val context = Context(scrupal, site)
            site.reactorFor(rh, subdomain) match {
              case Some(reactor) ⇒
                reactor.resultFrom(context,request)
              case None ⇒
                UnimplementedReactor(s"no reactor for $rh, $subdomain").resultFrom(context,request)
            }
          case (Some(site), None) ⇒
            val context = Context(scrupal, site)
            site.reactorFor(rh) match {
              case Some(reactor) ⇒
                reactor.resultFrom(context,request)
              case None ⇒
                UnimplementedReactor(s"no reactor for $rh").resultFrom(context, request)
            }
          case _ ⇒
            Future.successful { NotFound(s"site not found for request(host=${request.host}") }
        }
      } else {
        Future.successful { NotFound(s"request.path[${request.path}].startsWith(prefix[$prefix]) is false") }
      }
    }
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
      )
    ).as("text/javascript")
  }
}
