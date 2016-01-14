package router.scrupal.core

import play.api.mvc._
import play.twirl.api.Html
import scrupal.core._

import scala.concurrent.Future

class AdminController(scrupal : Scrupal) extends Controller {

  private def makePage(content: Html) : Future[Result] = {

    val siteMap = Map('localhost → "Local Host")

    val args = Map(
      "nav" → HtmlContent(admin.html.navbar(siteMap, Map('Core → "Scrupal Core"))),
      "header" → HtmlContent(admin.html.header()),
      "left" → HtmlContent(admin.html.left()),
      "right" → HtmlContent(admin.html.right()),
      "content" → HtmlContent(content),
      "footer" → HtmlContent(admin.html.footer()),
      "endscripts" → HtmlContent(Html(""))
    )

    val context = Context(scrupal)

    StandardThreeColumnLayout.apply(context, args).map { html ⇒ Ok(html) }(scrupal.executionContext)
  }

  def index() = Action.async { request : Request[AnyContent] ⇒
    makePage(admin.html.introduction())
  }

  def site(id : String) = Action.async { request : Request[AnyContent] ⇒
    makePage(admin.html.site(scrupal.DefaultLocalHostSite.data))
  }

  def module(id: String) = Action.async { request : Request[AnyContent] ⇒
    makePage(admin.html.module())
  }

}
