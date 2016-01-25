package scrupal.html

import play.api.{Application, Mode}
import scrupal.core.Scrupal

import scalatags.Text.all._

/** Title Of Thing.
  *
  * Description of thing
  */
class ScalaJS(implicit scrupal: Scrupal) {

  def javascriptDependencies(projectName : String) : HtmlContents = {

    val jsdeps = s"${projectName.toLowerCase}-jsdeps"

    def jsdepsScript(name : String) : HtmlElement = {
      script(src:=assets.projectjs(name).url, `type`:="text/javascript")
    }

    scrupal.withApplication { implicit application: Application ⇒
      import play.api.Play.resource
      if(resource(s"public/$jsdeps.min.js").isDefined) {
        Seq(jsdepsScript(s"public/$jsdeps.min.js"))
      } else if(resource(s"public/$jsdeps.js").isDefined) {
        Seq(jsdepsScript(s"$jsdeps.js"))
      } else {
        Seq.empty[HtmlElement]
      }
    }
  }

  def launcher(projectName : String) : HtmlElement = {
    script(src:=assets.projectjs(s"${projectName.toLowerCase}-launcher.js").url, `type`:="text/javascript")
  }

  def selectScript(projectName : String) : HtmlElement = {
    val name = scrupal.environment.mode match {
      case Mode.Prod ⇒
        s"${projectName.toLowerCase}-opt.js"
      case _ ⇒
        s"${projectName.toLowerCase}-fastopt.js"
    }
    script(src:=assets.projectjs(name).url,`type`:="text/javascript")
  }

  def projectScript(projectName : String) : HtmlContents = {
    javascriptDependencies(projectName) ++ Seq(
      selectScript(projectName),
      launcher(projectName)
    )
  }
}
