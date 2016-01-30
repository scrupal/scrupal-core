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

import scrupal.core.{Scrupal, HtmlContent}
import scrupal.test.SharedTestScrupal

import scalatags.Text.all._


class LayoutSpec extends ValidatingSpecification("Layout") with SharedTestScrupal {

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

  "PageLayout" should {
    "have some test examples" in {
      pending
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
    "have some test examples" in {
      pending
    }
  }

}
