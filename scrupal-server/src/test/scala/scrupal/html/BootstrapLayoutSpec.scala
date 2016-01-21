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

import scrupal.core.HtmlContent

import scalatags.Text.all._

class BootstrapLayoutSpec extends ValidatingSpecification("Bootstrap") {

  "BootstrapLayout" should {
    "have some test examples" in {
      pending
    }
  }

  "SimpleBootstrapLayout" should {
    "have some test examples" in {
      pending
    }
  }

  "ThreeColumnBootstrapLayout" should {
    "generate valid text content" in {
      val args = Map[String,HtmlContent](
        "nav" → HtmlContent(span("nav-value")),
        "header" → HtmlContent(span("header-value")),
        "left" → HtmlContent(span("left-value")),
        "right" → HtmlContent(span("right-value")),
        "contents" → HtmlContent(span("content-value")),
        "footer" → HtmlContent(span("footer-value")),
        "endscripts" → HtmlContent(span("endscripts-value"))
      )

      val future = context.scrupal.threeColumnBootstrapLayout.page(context, args).map { body : String ⇒
        log.info(s"3col body is: $body")
        body.contains("nav-value") must beTrue
        body.contains("header-value") must beTrue
        body.contains("left-value") must beTrue
        body.contains("right-value") must beTrue
        body.contains("content-value") must beTrue
        body.contains("footer-value") must beTrue
        body.contains("endscripts-value") must beTrue
        validate("ThreeColumnBootstrapLayout", body)
      }(scrupal.executionContext)
      await(future)
    }

    "fail if all parameters are not given" in {
      val args = Map(
        "navheader" → HtmlContent(span("navheader-value")),
        "header" → HtmlContent(span("header-value")),
        "right" → HtmlContent(span("right-value")),
        "footer" → HtmlContent(span("footer-value"))
      )
      val future = context.scrupal.threeColumnBootstrapLayout.page(context, args).map { body : String ⇒
        failure("Page generation should have failed")
      }(scrupal.executionContext)
      await(future) must throwA[IllegalArgumentException]
    }

    "have sane members" in {
      context.scrupal.threeColumnBootstrapLayout.id must beEqualTo('ThreeColumnBootstrapLayout)
      context.scrupal.threeColumnBootstrapLayout.arrangementDescription.nonEmpty must beTrue
      context.scrupal.threeColumnBootstrapLayout.description.nonEmpty must beTrue
    }
  }
}
