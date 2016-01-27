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

import play.api.libs.json.{JsString, Json}
import play.api.test.FakeRequest
import scrupal.core.{ThrowableContent, Stimulus}
import scrupal.test.SharedTestScrupal

import scalatags.Text.all._

class UtilitiesSpec extends ValidatingSpecification("Utilities") with SharedTestScrupal {

  "unauthorized" should {
    "lay out content properly" in {
      val html = unauthorized("this page")().render
      html.contains("this page") must beTrue
      validate("unauthorized", html, "div")
    }
  }

  "danger" should {
    "lay out content properly" in {
      val html = danger(span("this is dangerous"))().render
      html.contains("this is dangerous") must beTrue
      validate("danger", html, "div")
    }
  }

  "warning" should {
    "lay out content properly" in {
      val html = warning(span("this is warning"))().render
      html.contains("this is warning") must beTrue
      validate("warning", html, "div")
    }
  }

  "success" should {
    "lay out content properly" in {
      val html = successful(span("this is successful"))().render
      html.contains("this is successful") must beTrue
      validate("warning", html, "div")
    }
  }

  "exception" should {
    "lay out content properly" in {
      val xcptn = new IllegalArgumentException("bad args")
      val html = exception("testing", xcptn)().render
      html.contains("IllegalArgumentException") must beTrue
      html.contains("bad args") must beTrue
      validate("exception", html, "div")
    }
  }

  "throwable" should {
    "lay out content properly" in {
      val xcptn = new IllegalArgumentException("foo")
      val html = throwable(xcptn)().render
      html.contains("IllegalArgumentException") must beTrue
      html.contains("foo") must beTrue
      validate("throwable", html, "div")
    }
  }

  "display_context_table" should {
    "lay out content properly" in {
      val html = display_context_table(context).render
      html.contains("Context") must beTrue
      validate("display_context_table", html, "div")
    }
  }

  "display_throwable_content" should {
    "lay out content properly" in {
      val xcptn = new IllegalArgumentException("foo")
      val dtc = display_throwable_content(ThrowableContent(xcptn))
      val html = dtc().render
      html.contains("IllegalArgumentException") must beTrue
      validate("display_throwable_content", html, "div")
    }
  }

  "display_stimulus_table" should {
    "lay out content properly" in {
      val stimulus = Stimulus(context,FakeRequest("GET", "/"))
      val html = display_stimulus_table(stimulus)(context).render
      validate("display_stimulus_table", html, "div")
    }
  }

  "debug_footer" should {
    "lay out content properly" in {
      val html = debug_footer(context).render
      validate("debug_footer", html, "div")
    }
  }

  "json_value" should {
    "lay out content properly" in {
      val jsv = JsString("first value")
      val html = json_value(jsv)().render
      html.contains("first value") must beTrue
      validate("json_value", html, "div")
    }
  }

  "json_document_panel" should {
    "lay out content properly" in {
      val jso = Json.obj("one" → "first value")
      val html = json_document_panel("title",jso)().render
      validate("json_document_panel", html, "div")
    }
  }

  "reactific_copyright" should {
    "lay out content properly" in {
      val html = reactific_copyright(context).render
      html.contains("Reactific") must beTrue
      validate("reactific_copyright", html, "div")
    }
  }

  "scrupal_stats" should {
    "lay out content properly" in {
      val html = scrupal_stats(context).render
      validate("scrupal_stats", html, "div")
    }
  }
}
