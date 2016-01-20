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

import scrupal.core.{HtmlContent, Scrupal}

import scalatags.Text.all._
import polymer._

class PolymerSpec extends ValidatingSpecification("Polymer") {

  class TestPolymerLayout(name : String)(implicit val scrupal: Scrupal)
    extends { val id : Symbol = Symbol(name) } with PolymerLayout {
    def description: String = "Test Polymer Layout"
  }

  "Polymer" should {
    val tpl = new TestPolymerLayout("TestPolymerLayout")
    "register the TestPolymerLayout" in {
      scrupal.layouts.contains('TestPolymerLayout) must beTrue
    }

    "have polymer iron elements" in {
      val content = div(
        `iron-a11y-announcer`,
        `iron-a11y-keys-behavior`,
        `iron-a11y-keys`,
        `iron-ajax`,
        `iron-autogrow-textarea`,
        `iron-autogrow-textarea`,
        `iron-behaviors`,
        `iron-checked-element-behavior`,
        `iron-collapse`,
        `iron-component-page`,
        `iron-demo-helpers`,
        `iron-doc-viewer`,
        `iron-dropdown`,
        `iron-fit-behavior`,
        `iron-flex-layout`,
        `iron-form`,
        `iron-form-element-behavior`,
        `iron-icon`,
        `iron-icons`,
        `iron-iconset`,
        `iron-iconset-svg`,
        `iron-image`,
        `iron-input`,
        `iron-jsonp-library`,
        `iron-label`,
        `iron-list`,
        `iron-localstorage`,
        `iron-media-query`,
        `iron-menu-behavior`,
        `iron-meta`,
        `iron-overlay-behavior`,
        `iron-page-url`,
        `iron-pages`,
        `iron-range-behavior`,
        `iron-resizable-behavior`,
        `iron-selector`,
        `iron-signals`,
        `iron-swipeable-container`,
        `iron-test-helpers`,
        `iron-validatable-behavior`,
        `iron-validator-behavior`
      )
      val args = Map("content" → HtmlContent(content), "endscripts" → HtmlContent(emptyContents))
      val future = tpl.page(context, args)
      val result = await(future)
      result.nonEmpty must beTrue // TODO: Nu.Validator doesn't understand web components yet
    }

    "have polymer paper elements" in {
      val content = div(
        `paper-badge`,
        `paper-behaviors`,
        `paper-button`,
        `paper-card`,
        `paper-checkbox`,
        `paper-dialog`,
        `paper-dialog-behavior`,
        `paper-dialog-scrollable`,
        `paper-drawer-panel`,
        `paper-dropdown-menu`,
        `paper-fab`,
        `paper-header-panel`,
        `paper-icon-button`,
        `paper-input`,
        `paper-item`,
        `paper-listbox`,
        `paper-material`,
        `paper-menu`,
        `paper-menu-button`,
        `paper-progress`,
        `paper-radio-button`,
        `paper-radio-group`,
        `paper-ripple`,
        `paper-scroll-header-panel`,
        `paper-slider`,
        `paper-spinner`,
        `paper-styles`,
        `paper-tabs`,
        `paper-toast`,
        `paper-toggle-button`,
        `paper-toolbar`,
        `paper-tooltip`
      )
      val args = Map("content" → HtmlContent(content), "endscripts" → HtmlContent(emptyContents))
      val future = tpl.page(context, args)
      val result = await(future)
      result.nonEmpty must beTrue // TODO: Nu.Validator doesn't undrestand web components yet
    }
  }
}
