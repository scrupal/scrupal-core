package scrupal.core

import play.api.i18n.MessagesApi
import play.api.mvc._

/** Controller For Site API Requests */
class APIController(val scrupal : Scrupal, val messagesApi : MessagesApi) extends ScrupalController {

  val pathPrefix: String = "api"

  def contextFor(thing: String, request: RequestHeader): Option[Context] = {
    scrupal.siteForRequest(request) match {
      case Some(site) ⇒
        Some(Context(scrupal, site))
      case _ ⇒
        Some(Context(scrupal))
    }
  }

  def reactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = {
    context.site match {
      case Some(site) ⇒
        site.reactorFor(request)
      case None ⇒
        None
    }
  }
}
