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
import scrupal.test.SharedTestScrupal

import scalatags.Text.all._

class PolymerLayoutSpec extends ValidatingSpecification("Polymer") with SharedTestScrupal {

  class TestPolymerLayout(name : String, content: HtmlContents)(implicit val scrupal: Scrupal)
    extends { val id : Symbol = Symbol(name) } with PolymerLayout {
    def description: String = "Test Polymer Layout"
    override def contents(args : Arguments) : HtmlContents = content
  }

  "Polymer" should {
    "register the TestPolymerLayout" in {
      val tpl = new TestPolymerLayout("TestPolymerLayout", emptyContents)
      scrupal.layouts.contains('TestPolymerLayout) must beTrue
    }

    "have polymer iron elements" in {
      import Polymer.iron._
      val content = div(
        `a11y-announcer`,
        `a11y-keys-behavior`,
        `a11y-keys`,
        `ajax`,
        `autogrow-textarea`,
        `autogrow-textarea`,
        `behaviors`,
        `checked-element-behavior`,
        `collapse`,
        `component-page`,
        `demo-helpers`,
        `doc-viewer`,
        `dropdown`,
        `fit-behavior`,
        `flex-layout`,
        `form`,
        `form-element-behavior`,
        `icon`,
        `icons`,
        `iconset`,
        `iconset-svg`,
        `image`,
        `input`,
        `jsonp-library`,
        `label`,
        `list`,
        `localstorage`,
        `media-query`,
        `menu-behavior`,
        `meta`,
        `overlay-behavior`,
        `page-url`,
        `pages`,
        `range-behavior`,
        `resizable-behavior`,
        `selector`,
        `signals`,
        `swipeable-container`,
        `test-helpers`,
        `validatable-behavior`,
        `validator-behavior`
      )
      val tpl = new TestPolymerLayout("PolymerIron", content)
      val future = tpl.page(context, Map())
      val result = await(future)
      result.nonEmpty must beTrue // TODO: Nu.Validator doesn't understand web components yet
    }

    "have polymer paper elements" in {
      import Polymer.paper._
      val content = div(
        `badge`,
        `behaviors`,
        `button`,
        `card`,
        `checkbox`,
        `dialog`,
        `dialog-behavior`,
        `dialog-scrollable`,
        `drawer-panel`,
        `dropdown-menu`,
        `fab`,
        `header-panel`,
        `icon-button`,
        `input`,
        `item`,
        `listbox`,
        `material`,
        `menu`,
        `menu-button`,
        `progress`,
        `radio-button`,
        `radio-group`,
        `ripple`,
        `scroll-header-panel`,
        `slider`,
        `spinner`,
        `styles`,
        `tabs`,
        `toast`,
        `toggle-button`,
        `toolbar`,
        `tooltip`
      )
      val tpl = new TestPolymerLayout("PolymerPaper", content)
      val future = tpl.page(context, Map())
      val result = await(future)
      result.nonEmpty must beTrue // TODO: Nu.Validator doesn't understand web components yet
    }
  }
}
