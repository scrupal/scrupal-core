package router.scrupal.core

import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import scrupal.core.WithCoreSchema
import scrupal.utils.ScrupalComponent

/** A Generic Play Controller For Use With Scrupal.
  * This just bundles together some things that all the Scrupal controllers use.
  */
trait ScrupalController extends Controller with I18nSupport with WithCoreSchema with ScrupalComponent {

}
