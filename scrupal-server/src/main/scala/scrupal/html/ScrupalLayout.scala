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
    "A Bootstrap 3 page in three columns with navigation bar, header, fluid centered content, " +
      "left and right sidebars, footer, and Scrupal React.js Javascript"

  override def endScripts(args: Arguments) : HtmlContents = {
    super.endScripts(args) ++ scalajs.projectScript("scrupal-client")
  }

  override def contents(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "scrupal")
  }
}
