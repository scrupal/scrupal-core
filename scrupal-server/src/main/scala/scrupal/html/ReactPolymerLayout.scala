package scrupal.html

import scrupal.core._

import scalatags.Text.all._

/** Single Page Application Layout
  * A layout for doing a Single Page Application with Scrupal REST Back End, React.js, Scala.js, and Polymer
  * for web components.
  */
case class ReactPolymerLayout(appName : String = "")(implicit val scrupal : Scrupal) extends PolymerLayout {
  def id: Identifier = 'ScrupalLayout

  val scalajs = new ScalaJS()(scrupal)

  val description: String =
    "A Polymer + React layout for Single Page Application type pages"

  final override def arrangementDescription: Map[String,String] = Map(
    "contents" → "The main content",
    "endscripts" → "Scripts to be loaded at the end of the page"
  )

  override def contents(args: Arguments) : HtmlContents = {
    div(scalatags.Text.all.id := "react-polymer-app")
  }

  final override def header(args: Arguments) : HtmlContents = {
    emptyContents
  }

  final override def footer(args: Arguments) : HtmlContents = {
    emptyContents
  }

  override def endScripts(args: Arguments) : HtmlContents = {
    super.endScripts(args) ++ scalajs.projectScript("scrupal-react-polymer") ++
      { if (appName.nonEmpty) scalajs.projectScript(appName) else emptyContents }
  }
}
