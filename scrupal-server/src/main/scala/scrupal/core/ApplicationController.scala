package scrupal.core

import play.api.i18n.MessagesApi
import play.api.mvc._

/** Controller For Application Requests */
class ApplicationController(val scrupal : Scrupal, val messagesApi : MessagesApi) extends ScrupalController {

  val pathPrefix: String = "app"

  def reactorFor(context: Context, thing: String, request: RequestHeader): Option[Reactor] = {
    None
  }

  def contextFor(thing: String, request: RequestHeader): Option[Context] = {
    None
  }
}
