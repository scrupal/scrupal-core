package scrupal.core

import japgolly.scalajs.react.extra.router.BaseUrl
import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.raw

import scala.concurrent.Future
//import scala.async.Async._


import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

/** Single Page Application
  *
  * This is how we get the ball rolling. This JSApp is recognized by Scala.js and it generates the launcher script
  * fragment like this:
  * {{{
     ((typeof global === "object" && global &&
         global["Object"] === Object) ? global : this)["scrupal"]["core"]["SinglePageApp"]().main();
  * }}}
  * to invoke the main method. We place that fragment at the bottom of the HTML page in a script element so that
  * the application is launched towards the end of page loading.
  */
object ScrupalJsApp extends js.JSApp {

  /** main method
    * This method is invoked from the bottom of each ScrupalJsAPp page that is loaded into the browser. Such pages
    * should have
    * SinglePageAppLayout that causes this to be invoked (via the Launcher) defines three div elements named
    * spa-header, spa-content, and spa-footer. We just locate these in the DOM document and then pass them to the
    * load method to do the actual loading.
    */
  @JSExport
  override def main(): Unit = {
    val application_element = dom.document.getElementById("scrupal-jsapp").asInstanceOf[html.Div]
    val application_name = application_element.getAttribute("data-appname")
    clearNode(application_element)
    loadApplication(application_name, application_element)
  }

  import dom.ext._
  import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

  def clearNode(node : raw.Node) : Unit = {
    while (node.firstChild != null) {
      node.removeChild(node.firstChild)
    }

  }

  /** Load application content.
    * This method gets the application loaded into the browser. We consider the application to have three content
    * portions, a header, the main content, and a footer. T
    *
    * @param h The header element (html.Div)
    * @param c The content element (html.Div)
    * @param f The footer element (html.Div)
    */
  def loadApplication(application_name: String, element_to_set: html.Div) : Unit = {
    val baseUrl = BaseUrl.fromWindowUrl(x⇒x).rtrim_/.value
    val future = Ajax.get(baseUrl + "/config").map {
      case xhr if xhr.responseType == "json" || xhr.responseType == "" ⇒
        val obj = JSON.parse(xhr.responseText)
        element_to_set.appendChild(
          div (s"Application '$application_name' configuration is: ${JSON.stringify(obj)}").render
        )
      case xhr ⇒
        xhr.responseType
        val msg = s"Type: ${xhr.responseType}, State:${xhr.readyState}, Status:${xhr.status}(${xhr.statusText})"
        throw new Exception(s"Unrecognized response from server: $msg")
    } recover {
      case xcptn: Throwable ⇒
        element_to_set.appendChild(
          div(s"Application '$application_name' could not be configured from $baseUrl/config: $xcptn").render
        )
    }
    // async { await(future) }

/*    Ajax.get(url).map {
      case xhr ⇒
        xhr.responseType
    }*/
    // dom.document.createElement("div").asInstanceOf[html.Element]
    // import router.ApplicationRouter._
    // router() render dom.document.getElementById("scrupal")
    /*
    for ( hf ← header ; cf ← content ; ff ← footer) {
      h.appendChild(hf.render)
      c.appendChild(cf.render)
      f.appendChild(ff.render)
    }
    */
  }

  type STElement = TypedTag[dom.Element]

  def header : Future[STElement] = {
    Future.successful { div("Header") }
  }

  def content: Future[STElement] = {
    val url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=44db6a862fba0b067b1930da0d769e98"
    Ajax.get(url).map{ case xhr => pre(xhr.responseText) }
  }

  def footer : Future[STElement] = {
    Future.successful { div("Footer") }
  }
}
