package scrupal.admin

import play.api.routing.sird._
import scrupal.core._

class AdminUserProvider extends {
  val id = 'AdminUserProvider
} with Provider with Enablee {
  def provide = {
    case GET(p"/user/") ⇒ Reactor.from { Response("foo", Successful) }
  }
}
