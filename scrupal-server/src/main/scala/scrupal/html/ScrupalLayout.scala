package scrupal.html

import scrupal.core._

import scalatags.Text.all._

/** Title Of Thing.
  *
  * Description of thing
  */
class ScrupalLayout(implicit val scrupal : Scrupal) extends PolymerLayout {
  def id: Identifier = 'ScrupalLayout

  val scalajs = new ScalaJS()(scrupal)

  val description: String =
    "A Polymer + React layout for most SPA type page"

  override def endScripts(args: Arguments) : HtmlContents = {
    super.endScripts(args) ++ scalajs.projectScript("scrupal-client")
  }

  override def contents(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "scrupal")
  }
}
