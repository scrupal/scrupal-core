package scrupal.core

import org.scalajs.dom
import org.scalajs.dom.html

import scala.concurrent.Future

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/** Single Page Application
  *
  * This is how we get the ball rolling. This JSApp is recognized by Scala.js and it generates the launcher script
  * fragment like this:
  * {{{
     ((typeof global === "object" && global &&
         global["Object"] === Object) ? global : this)["scrupal"]["core"]["ReactPolymerApp"]().main();
  * }}}
  * to invoke the main method. We place that fragment at the bottom of the HTML page in a script element so that
  * the application is launched towards the end of page loading.
  */
object ReactPolymerApp extends {
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  /** main method
    * This method is invoked from the bottom of each SinglePageApp page that is loaded into the browser. The
    * SinglePageAppLayout that causes this to be invoked (via the Launcher) defines three div elements named
    * spa-header, spa-content, and spa-footer. We just locate these in the DOM document and then pass them to the
    * load method to do the actual loading.
    */
  def fakeMain(): Unit = {
    val app = dom.document.getElementById("polymer-react-app").asInstanceOf[html.Div]
    application.map { a ⇒ app.appendChild(a.render) }
  }


  type STElement = TypedTag[dom.Element]

  def application : Future[STElement] = {
    Future.successful { div("application") }
  }
}
