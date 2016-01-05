package scrupal.core

import play.twirl.api.{JavaScript, Txt, Html}

import scrupal.core.default.html.unauthorized
import scrupal.core.html.{throwable, master}
import scrupal.core.layout.html.{standardThreeColumn, defaultHtml}
import scrupal.test.{HTML5Validator, ScrupalSpecification}

case class PageHeadContext(ctxt: Context) extends Context {
  implicit def scrupal : Scrupal = ctxt.scrupal
  override def site : Option[Site] = ctxt.site
  override def siteName : String = ctxt.siteName
  override val favicon: String = ctxt.favicon
  override val themeProvider : String = ctxt.themeProvider
  override val themeName : String = ctxt.themeName
  override val description : String = ctxt.description
  override val user : String = ctxt.user
  override def pageHeadTags : PageHeadTags = PageHeadTags(
    "Page Title",
    base = Some(BaseTag("http://example.com/", "_self")),
    meta = MetaTags(Some("description"), Some("author"),Some("generator"),Some("application"),Seq("key1","key2")),
    links = LinkTags(Some("http://alternate.com"),Some("http://author.com"),
      Some("http://license.com"),Some(IconLinkTag("http://icon.com/top.gif",16,16,"image/gif")),
      Seq("http://scripts.com/1.js","http://scripts.com/2.js"),Seq("http://sheets.com/1.css")),
    style = Some(Txt("txt")),
    javascript = Some(JavaScript("function foo(){return 0;}"))
  )
}

class TemplatesSpec extends ScrupalSpecification("master") {

  "master" should {
    "generate a properly formed HTML5 page" in {
      val html = master(context.pageHeadTags)(Html("<p>Foo</p>"))
      html.contentType must beEqualTo("text/html")
      HTML5Validator.validateDocument(html.body) must beTrue
    }
    "generate head elements with proper content" in {
      val ctxt = PageHeadContext(context)
      val html = master(ctxt.pageHeadTags)(Html("<p>Foo</p>"))
      HTML5Validator.validateDocument(html.body) must beTrue
      // html.body.replaceAll("^\\s*$","") must beEqualTo(expected)
    }
  }

  val expected =
    """<!DOCTYPE html>
      |<html>
      |<head>
      |  <title>Page Title</title>
      |  <base href="http://example.com/" target="_self">
      |  <meta charset="UTF-8">
      |  <meta name="description" content="description">
      |  <meta name="author" content="author">
      |  <meta name="generator" content="generator">
      |  <meta name="application-name" content="application">
      |  <meta name="keywords" content="key1,key2">
      |  <link rel="alternate" href="http://alternate.com." type="text/html">"
      |  <link rel="author" href="http://author.com" type="text/html">
      |  <link rel="license" href="http://license.com" type="text/html">
      |  <link rel="icon" href="http://icon.com/top.gif" type="image/gif" sizes="16x16" >
      |  <link rel="stylesheet" type="text/css" href="http://sheets.com/1.css">
      |  <script src="http://scripts.com/1.js" type="text/javascript"></script><script src="http://scripts.com/2.js" type="text/javascript"></script>
      |  <script>function foo(){return 0;}</script>
      |  <style>txt</style>
      |</head>
      |<body>
      |<p>Foo</p>
      |</body>
      |</html>
      |""".stripMargin

  "throwable" should {
    "format an exception properly" in {
      val xcptn = new IllegalArgumentException("foo")
      val html = throwable(xcptn)
      HTML5Validator.validateFragment(html.body) must beTrue
    }
  }
  "defaultHtml Layout" should {
    "lay out content properly" in {
      val html = defaultHtml(context, Map("one" → HtmlContent(Html("<p>foo</p>"))))
      HTML5Validator.validateFragment(html.body) must beTrue
    }
  }
  "standardThreeColumn Layout" should {
    "lay out content properly" in {
      val html = standardThreeColumn(context, context.pageHeadTags, args = Map(
        "navheader" → HtmlContent(Html("<p>navheader</p>")),
        "navbar" → HtmlContent(Html("<p>navbar</p>")),
        "header" → HtmlContent(Html("<p>header</p>")),
        "left" → HtmlContent(Html("<p>left</p>")),
        "right" → HtmlContent(Html("<p>right</p>")),
        "content" → HtmlContent(Html("<p>content</p>")),
        "footer" → HtmlContent(Html("<p>footer</p>"))
      ))
      HTML5Validator.validateDocument(html.body) must beTrue
    }
  }
  "unauthorized" should {
    "lay out content properly" in {
      val html = unauthorized("this page")
      HTML5Validator.validateFragment(html.body) must beTrue

    }
  }
}
