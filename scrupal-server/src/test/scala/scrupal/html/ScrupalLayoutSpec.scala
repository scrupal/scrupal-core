package scrupal.html

import scalatags.Text.all._

import scrupal.core.HtmlContent

/** Test Cases For ScrupalLayout */
class ScrupalLayoutSpec extends ValidatingSpecification("ScrupalLayout") {

  def makeScrupalArgs(contents : HtmlContents) = {
    Map(
      "header" → HtmlContent(emptyContents),
      "contents" → HtmlContent(contents),
      "footer" → HtmlContent(emptyContents),
      "endscripts" → HtmlContent(emptyContents)

    )
  }

  "ScrupalLayout" should {
    "produce valid HTML" in {
      val content = div()
      val future = scrupal.scrupalLayout.page(context, makeScrupalArgs(content))
      val result = await(future)
      result.contains("scrupal-client") must beTrue
      // FIXME: nu.validator can't handle HTML Imports yet
      // validate("ScrupalLayout", result)
      result.contains("rel=\"import\"")
    }
  }
}
