package scrupal.admin

import scrupal.core.ApplicationProvider

class AdminProvider extends { val id = 'admin } with ApplicationProvider {
  def name: String = id.name

  object moduleProvider extends AdminModuleProvider
  object siteProvider extends AdminSiteProvider
  object scrupalProvider extends AdminScrupalProvider
  object userProvider extends AdminUserProvider

  override val singularRoutes: ReactionRoutes = {
    siteProvider.provide.
      orElse(moduleProvider.provide).
      orElse(userProvider.provide).
      orElse(scrupalProvider.provide).
      orElse(super.singularRoutes)
  }

}
