package scrupal.html

import play.api.{Application, Mode}
import scrupal.core.Scrupal

import scalatags.Text.all._

/** Title Of Thing.
  *
  * Description of thing
  */
class ScalaJS(implicit scrupal: Scrupal) {


  def javascriptDependencies(projectName : String,
    assets:      String => String = fileName => s"/assets/$fileName",
    resources:   String => String = fileName => s"/public/$fileName") : HtmlContents = {

    def jsdepsScript(jsdeps: String) : HtmlElement = {
      script(src:=assets(jsdeps), `type`:="text/javascript")
    }

    val jsdeps = s"${projectName.toLowerCase}-jsdeps"

    scrupal.withApplication { implicit application: Application ⇒
      import play.api.Play.resource
      if(resource(resources(s"${jsdeps}.min.js")).isDefined) {
        Seq(jsdepsScript(s"${jsdeps}.min.js"))
      } else if(resource(resources(s"${jsdeps}.js")).isDefined) {
        Seq(jsdepsScript(s"${jsdeps}.js"))
      } else {
        Seq.empty[HtmlElement]
      }
    }
  }

  def launcher(projectName : String, assets: String => String = fileName => s"/assets/$fileName") : HtmlElement = {
    script(src:=assets(s"${projectName.toLowerCase}-launcher.js"),`type`:="text/javascript")
  }

  def selectScript(projectName : String, assets: String => String = fileName => s"/assets/$fileName") : HtmlElement = {
    val name = scrupal.environment.mode match {
      case Mode.Prod ⇒
        s"${projectName.toLowerCase}-opt.js"
      case _ ⇒
        s"${projectName.toLowerCase}-fastopt.js"
    }
    script(src:=assets(name),`type`:="text/javascript")
  }

  def projectScript(projectName : String,
    assets:      String => String = fileName => s"/assets/$fileName",
    resources:   String => String = fileName => s"/public/$fileName") : HtmlContents = {
    javascriptDependencies(projectName, assets, resources) ++ Seq(
      selectScript(projectName, assets),
      launcher(projectName, assets)
    )
  }
}
