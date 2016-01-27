package scrupal.admin

import play.api.routing.sird._

import scrupal.core._
import scrupal.html._

import scalatags.Text.all._

class AdminScrupalProvider(val scrupal : Scrupal) extends {
  val id = 'AdminScrupalProvider
} with Provider with Enablee with WithCoreSchema {
  def provide = {
    case GET(p"/help") ⇒ Reactor.from { Response(help(), Successful) }
    case GET(p"/")     ⇒ Reactor.from { Response(help(), Successful) }
  }

  def help() : HtmlContents = {
    p("help")
  }
}
