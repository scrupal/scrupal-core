package scrupal.html

import scrupal.core._

import scalatags.Text.all._

/** Single Page Application Layout
  * A layout for doing a Single Page Application with Scrupal REST Back End, React.js, Scala.js, and Polymer
  * for web components.
  */
case class ReactPolymerLayout(implicit val scrupal : Scrupal) extends PolymerLayout {
  def id: Identifier = 'ReactPolymerLayout

  val scalajs = new ScalaJS()(scrupal)

  val description: String =
    "A Polymer + React layout for Single Page Applications"

  final override def argumentDescription: Map[String,String] = Map(
    "appName" → "The name of the application to load"
  )

  override def contents(args: Arguments) : HtmlContents = {
    val appName : String = args.args.getOrElse("appName","")
    div(scalatags.Text.all.id := "scrupal-jsapp", data("appname"):=appName)
  }

  override def epilogue(args: Arguments) : HtmlContents = {
    super.epilogue(args) ++ scalajs.projectScript("scrupal-jsapp")
  }
}
