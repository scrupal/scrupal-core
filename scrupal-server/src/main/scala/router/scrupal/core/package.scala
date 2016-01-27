package router.scrupal

package object core {

  // NOTE: For whatever reason, Play insists that the controllers be in the router package.
  // NOTE: Without these type definitions pointing them to their actual location (as given in th routes file)
  // NOTE: compilation fails with, e.g. "APIController is not a member of package router.scrupal.core"

  type APIController = _root_.scrupal.core.APIController
  type ApplicationController = _root_.scrupal.core.ApplicationController
  type Assets = _root_.scrupal.core.Assets
}
