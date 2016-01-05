package scrupal.core

import play.api.libs.json.JsString
import play.twirl.api.Html
import scrupal.test.{HTML5Validator, ScrupalSpecification}

import scala.concurrent.ExecutionContext.Implicits.global


class LayoutSpec extends ScrupalSpecification("Layout") {

  "DefaultHtmlLayout" should {
    "generate valid HTML" in {
      val future = DefaultHtmlLayout(context, Map("one" → HtmlContent(Html("one")))).map { html ⇒
        HTML5Validator.validate(html.body).isEmpty must beTrue
      }
      await(future)
    }
    "have sane members" in {
      DefaultHtmlLayout.id must beEqualTo('DefaultHtmlLayout)
      DefaultHtmlLayout.arrangementDescription.nonEmpty must beTrue
      DefaultHtmlLayout.description.nonEmpty must beTrue
    }
  }
  "DefaultTextlLayout" should {
    "generate valid text content" in {
      val future = DefaultTextLayout(context, Map("one" → TextContent("mapped-from-one"))).map { text ⇒
        text.body.contains("mapped-from-one") must beTrue
      }
      await(future)
    }
    "have sane members" in {
      DefaultTextLayout.id must beEqualTo('DefaultTextLayout)
      DefaultTextLayout.arrangementDescription.nonEmpty must beTrue
      DefaultTextLayout.description.nonEmpty must beTrue
    }
  }

  "DefaultJsonLayout" should {
    "generate valid text content" in {
      val future = DefaultJsonLayout(context, Map("one" → JsonContent(JsString("mapped-from-one")))).map { text ⇒
        text.body.contains("mapped-from-one") must beTrue
      }
      await(future)
    }
    "have sane members" in {
      DefaultJsonLayout.id must beEqualTo('DefaultJsonLayout)
      DefaultJsonLayout.arrangementDescription.nonEmpty must beTrue
      DefaultJsonLayout.description.nonEmpty must beTrue
    }

  }

  "StandardThreeColumnLayout" should {
    "generate valid text content" in {
      val future = StandardThreeColumnLayout(context, Map(
        "navheader" → HtmlContent(Html("navheader-value")),
        "navbar" → HtmlContent(Html("<p>navbar-value</p>")),
        "header" → HtmlContent(Html("<p>header-value</p>")),
        "left" → HtmlContent(Html("<p>left-value</p>")),
        "right" → HtmlContent(Html("<p>right-value</p>")),
        "content" → HtmlContent(Html("<p>content-value</p>")),
        "footer" → HtmlContent(Html("<p>footer-value</p>"))
      )).map { html ⇒
        HTML5Validator.validate(html.body).isEmpty must beTrue
        html.body.contains("navheader-value") must beTrue
        html.body.contains("navbar-value") must beTrue
        html.body.contains("header-value") must beTrue
        html.body.contains("left-value") must beTrue
        html.body.contains("right-value") must beTrue
        html.body.contains("content-value") must beTrue
        html.body.contains("footer-value") must beTrue
      }
      await(future)
    }
    "fail if all parameters are not given" in {
      StandardThreeColumnLayout(context, Map(
        "navheader" → HtmlContent(Html("navheader-value")),
        "header" → HtmlContent(Html("<p>header-value</p>")),
        "right" → HtmlContent(Html("<p>right-value</p>")),
        "footer" → HtmlContent(Html("<p>footer-value</p>"))
      )) must throwA[IllegalArgumentException]
    }
    "have sane members" in {
      StandardThreeColumnLayout.id must beEqualTo('StandardThreeColumnLayout)
      StandardThreeColumnLayout.arrangementDescription.nonEmpty must beTrue
      StandardThreeColumnLayout.description.nonEmpty must beTrue
    }
  }
}
