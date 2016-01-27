package scrupal.admin

import play.api.routing.sird._

import scrupal.core._

class AdminModuleProvider extends {
  val id = 'AdminModuleProvider
} with Provider with Enablee {
  def provide = {
    case GET(p"") ⇒ Reactor.from { Response("foo", Successful) }
  }
}
