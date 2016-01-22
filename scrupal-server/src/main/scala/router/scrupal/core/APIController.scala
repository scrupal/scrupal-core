package router.scrupal.core

import com.reactific.helpers.Patterns
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scrupal.core._
import scrupal.utils.ScrupalComponent

import scala.concurrent.Future

/** Title Of Thing.
  *
  * Description of thing
  */
class APIController(val scrupal : Scrupal, val messagesApi : MessagesApi)
  extends Controller with I18nSupport with WithCoreSchema with ScrupalComponent {

  def entityAction(entityName: String, rest: String) = {
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
}
