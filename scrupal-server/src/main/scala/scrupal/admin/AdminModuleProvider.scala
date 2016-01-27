package scrupal.admin

import play.api.routing.sird._

import scrupal.core._

class AdminModuleProvider (val scrupal : Scrupal) extends {
  val id = 'AdminModuleProvider
} with Provider with Enablee with WithCoreSchema {
  def provide = {
    case GET(p"") ⇒ Reactor.from { Response("foo", Successful) }
  }
}
