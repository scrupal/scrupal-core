package router.scrupal.core

import play.api.i18n.MessagesApi
import play.api.mvc._
import play.api.routing.JavaScriptReverseRouter
import scrupal.core._

import scala.concurrent.Future

/** Title Of Thing.
  *
  * Description of thing
  */
class APPController(val scrupal : Scrupal, val messagesApi : MessagesApi) extends ScrupalController {

  def appGET(appName: String, rest : String) = appAction(appName, rest)
  def appPUT(appName: String, rest : String) = appAction(appName, rest)
  def appPOST(appName: String, rest : String) = appAction(appName, rest)
  def appOPTIONS(appName: String, rest : String) = appAction(appName, rest)
  def appDELETE(appName: String, rest : String) = appAction(appName, rest)
  def appHEAD(appName: String, rest : String) = appAction(appName, rest)

  protected def appAction(appName: String, rest: String) = {
    Action.async { implicit request : Request[AnyContent] ⇒
      val prefix = s"/app/"
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
