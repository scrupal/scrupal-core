package scrupal.html

import scalatags.Text.all._

import scrupal.core.{Context, HtmlContent}

/** Test Cases For ScrupalLayout */
class ReactPolymerLayoutSpec extends ValidatingSpecification("ReactPolymerLayout") {

  def makeScrupalArgs(contents : HtmlContents) = {
    Map(
      "header" → HtmlContent(emptyContents),
      "contents" → HtmlContent(contents),
      "footer" → HtmlContent(emptyContents),
      "endscripts" → HtmlContent(emptyContents)
    )
  }

  "ScrupalLayout" should {
    "produce valid HTML" in withScrupal("ScrupalLayout_validity") { scrupal ⇒
      val content = div()
      val future = scrupal.reactPolymerLayout.page(Context(scrupal), makeScrupalArgs(content))
      val result = await(future)
      result.contains("react-polymer-app") must beTrue
      // FIXME: nu.validator can't handle HTML Imports yet
      validate("ReactPolymerLayout", result)
      result.contains("rel=\"import\"")
    }
  }
}
