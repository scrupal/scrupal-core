package scrupal.html

import scalatags.Text.all._

import scrupal.core.{Context, HtmlContent}

/** Test Cases For ScrupalLayout */
class ReactPolymerLayoutSpec extends ValidatingSpecification("ReactPolymerLayout") {

  "ReactPolymerLayout" should {
    "produce valid HTML" in withScrupal("ScrupalLayout_validity") { scrupal ⇒
      val content = div()
      val future = scrupal.reactPolymerLayout.page(Context(scrupal), Map("appName" → "Test"))
      val result = await(future)
      result must contain("scrupal-jsapp")
      result must contain("rel=\"import\"")
      result must contain("data-appname=\"Test\"")
      validate("ReactPolymerLayout", result)
    }
  }
}
