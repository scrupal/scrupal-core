/**********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
  *                                                                                                                    *
  * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
  *                                                                                                                    *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
  * with the License. You may obtain a copy of the License at                                                          *
  *                                                                                                                    *
  *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
  *                                                                                                                    *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
  * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
  * the specific language governing permissions and limitations under the License.                                     *
  **********************************************************************************************************************/
package scrupal.html

import scrupal.core._
import scrupal.test.SharedTestScrupal

import scalatags.Text.all._


class AppPageLayoutSpec extends ValidatingSpecification("Layout") with SharedTestScrupal {

  class TestLayout1(implicit val scrupal : Scrupal) extends AppPageLayout {
    def id = 'TestLayout1
    def description : String = "Test Layout #1"
    def argumentDescription: Map[String,String] = Map("one"→"First Argument")
    override def layout(args : Arguments) : HtmlElement = div(args.args.get("one"))
  }

  class TestLayout2(implicit val scrupal : Scrupal) extends AppPageLayout {
    def id = 'TestLayout2
    def description : String = "Test Layout #2"
    def argumentDescription: Map[String, String] = Map("one" → "First Argument")
    override def headTag(args: Arguments): HtmlElement = head(meta(rel:="foo"))
  }

  class TestLayout3(implicit val scrupal : Scrupal) extends DetailedPageLayout {
    def id = 'TestLayout3
    def description : String = "Test Layout #3"
    override def argumentDescription: Map[String,String] = Map("one"→"First Argument")
  }

  "Layout" should {
    val tl1 = new TestLayout1()(scrupal)
    "Allow simple fragment overloading" in {
      val result = tl1.apply(context, Map("one" → "one")).map { html : HtmlElement ⇒
        html.render.contains("one") must beTrue
      }(scrupal.executionContext)
      await(result)
    }

    "register standard layouts automatically" in {
      scrupal.layouts.contains('DefaultPageLayout) must beTrue
      scrupal.layouts.contains('SimpleBootstrapLayout) must beTrue
      scrupal.layouts.contains('ThreeColumnBootstrapLayout) must beTrue
    }

    "register newly created layouts automatically" in {
      scrupal.layouts.contains('TestLayout1) must beTrue
    }
  }

  "DefaultPageLayout" should {
    val dpl = scrupal.defaultPageLayout
    "generate valid HTML" in {
      val future = dpl.page(context, Map("one" → "one"))
      val result = await(future)
      validate("DefaultPageLayout", result)
    }
    "have sane members" in {
      dpl.id must beEqualTo('DefaultPageLayout)
      dpl.argumentDescription.nonEmpty must beTrue
      dpl.description.nonEmpty must beTrue
    }
  }

  "DetailedPageLayout" should {
    case class TestDetailedPageLayout(id : Identifier)(implicit val scrupal : Scrupal) extends DetailedPageLayout {
      val description: String = "Detailed page layout test case"
      override def contents(args: Arguments) : HtmlContents = { span("This is the content") }
      override def baseTag(args : Arguments) : Option[BaseTag] = Some(BaseTag("http://foo.com", "_parent"))
      override def description(args : Arguments) : Option[String] = Some("description")
      override def authorName(args : Arguments) : Option[String] = Some("authorName")
      override def application(args : Arguments) : Option[String] = Some("application")
      override def keywords(args : Arguments) : Seq[String] = Seq("key1", "key2")
      override def otherMeta(args: Arguments) : Map[String,String] = Map("meta1" → "meta1")
      override def alternate(args: Arguments) : Option[String] = Some("alternate")
      override def authorLink(args: Arguments) : Option[String] = Some("http://www.authorLink.com")
      override def licenseLink(args: Arguments) : Option[String] = Some("http://www.licenseLink.com")
      override def favIcon(args: Arguments) : Option[String] = Some("favIcon")
      override def iconLink(args: Arguments) : Option[IconLinkTag] = Some(IconLinkTag("http://icons.com", 16, 16, "image/gif"))
      override def imports(args: Arguments) : Seq[String] = Seq("http://import.me")
      override def javascriptLinks(args : Arguments) : Seq[String] = Seq("http://land.of.js.com")
      override def stylesheetLinks(args : Arguments) : Seq[String] = Seq("http://land.of.css.com")
      override def scripts(args : Arguments) : Seq[String] = Seq("var foo = 1")
      override def sheets(args : Arguments) : Seq[String] = Seq(".serenity { display : none }")
      override def otherLinks(args : Arguments) : Seq[LinkTag] = Seq(LinkTag("prefetch", "http://my.pages.com/index.html"))
    }
    val tdpl = TestDetailedPageLayout('TestDetailedPageLayout)

    "have layout with two args" in {
      val elem = tdpl.layout(Context(scrupal), Map.empty[String,String])
      elem.render must startWith("<html>")
    }
    "contains overridden content" in {
      val content = tdpl.layout(Arguments(Context(scrupal), Map()))
      val c = content.render
      c must startWith("<html>")
      c must contain("""<title>Scrupal</title>""")
      c must contain("""<meta charset="UTF-8" />""")
      c must contain("""<span>This is the content</span>""")
      c must contain("""<base href="http://foo.com" target="_parent" />""")
      c must contain("""<meta name="description" content="description" />""")
      c must contain("""<meta name="author" content="authorName" />""")
      c must contain("""<meta name="generator" content="Scrupal" />""")
      c must contain("""<meta name="keywords" content="key1,key2"""")
      c must contain("""<meta name="meta1" content="meta1" />""")
      c must contain("""<link rel="alternate" type="text/html" href="alternate" />""")
      c must contain("""<link rel="author" type="text/html" href="http://www.authorLink.com" />""")
      c must contain("""<link rel="license" type="text/html" href="http://www.licenseLink.com" />""")
      c must contain("""<link rel="icon" href="http://icons.com" type="image/gif" sizes="16x16" />""")
      c must contain("""<link rel="shortcut icon" type="image/x-icon" href="favIcon" />""")
      c must contain("""<link rel="import" href="http://import.me" type="text/html" />""")
      c must contain("""<script src="http://land.of.js.com" type="text/javascript"></script>""")
      c must contain("""<link rel="stylesheet" href="http://land.of.css.com" type="text/css" media="screen" />""")
      c must contain("""<script>var foo = 1</script>""")
      c must contain("""<style>.serenity { display : none }</style>""")
      c must contain("""<link rel="prefetch" type="text/html" href="http://my.pages.com/index.html" />""")
      c must endWith("</html>")
      validate("Full Layout", "<!DOCTYPE html>" + c)
    }
  }

}
