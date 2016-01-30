package scrupal.admin

import play.api.routing.sird._

import scrupal.core._

class AdminProvider(implicit val scrupal: Scrupal) extends { val id = 'admin } with ApplicationProvider {
  def name: String = id.name

  object moduleProvider extends AdminModuleProvider
  object siteProvider extends AdminSiteProvider
  object scrupalProvider extends AdminScrupalProvider
  object userProvider extends AdminUserProvider

  override def singularRoutes: ReactionRoutes = {
    siteProvider.provide.
      orElse(moduleProvider.provide).
      orElse(userProvider.provide).
      orElse(scrupalProvider.provide).
      orElse(adminRoutes).
      orElse(super.singularRoutes)
  }

  def adminRoutes : ReactionRoutes = {
    case GET(p"/name") ⇒ Reactor.from {
      Response("Administration", Successful)
    }
  }

}
