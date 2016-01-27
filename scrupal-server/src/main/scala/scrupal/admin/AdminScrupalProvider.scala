package scrupal.admin

import play.api.routing.sird._

import scrupal.core._
import scrupal.html._

import scalatags.Text.all._

class AdminScrupalProvider extends {
  val id = 'AdminScrupalProvider
} with Provider with Enablee  {
  def provide = {
    case GET(p"/help") ⇒ Reactor.from { Response(help(), Successful) }
    case GET(p"/")     ⇒ Reactor.from { Response(help(), Successful) }
  }

  def help() : HtmlContents = {
    p("help")
  }
}
