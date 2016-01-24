package scrupal.html

import scrupal.core._

import scalatags.Text.all._

/** Single Page Application Layout
  * A layout for doing a Single Page Application with Scrupal REST Back End, React.js, Scala.js, and Polymer
  * for web components.
  */
class SinglePageAppLayout(implicit val scrupal : Scrupal) extends PolymerLayout {
  def id: Identifier = 'ScrupalLayout

  val scalajs = new ScalaJS()(scrupal)

  val description: String =
    "A Polymer + React layout for most SPA type page"

  override def contents(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "spa-content")
  }

  override def header(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "spa-header")
  }

  override def footer(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "spa-footer")
  }

  override def endScripts(args: Arguments) : HtmlContents = {
    super.endScripts(args) ++ scalajs.projectScript("scrupal-client")
  }
}
