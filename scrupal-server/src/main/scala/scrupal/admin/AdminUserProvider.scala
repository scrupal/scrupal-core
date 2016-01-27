package scrupal.admin

import play.api.routing.sird._
import scrupal.core._

class AdminUserProvider (val scrupal : Scrupal) extends {
  val id = 'AdminUserProvider
} with Provider with Enablee with WithCoreSchema {
  def provide = {
    case GET(p"/") ⇒ Reactor.from { Response("foo", Successful) }
  }
}
