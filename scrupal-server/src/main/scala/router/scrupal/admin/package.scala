package router.scrupal

package object admin {

  // NOTE: For whatever reason, Play insists that the controllers be in the router package.
  // NOTE: Without this type definitions pointing to the actual location (as given in th routes file)
  // NOTE: compilation fails with "AdminController is not a member of package router.scrupal.admin"

  type AdminController = _root_.scrupal.admin.AdminController

}
