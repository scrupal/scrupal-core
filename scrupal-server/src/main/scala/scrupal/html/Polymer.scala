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

import scalatags.Text
import scalatags.generic.Util
import scalatags.text.Builder

trait GenericPolymerIronTags[Bldr, Output <: FragT, FragT] extends Util[Bldr, Output, FragT] {
  import scalatags.generic.TypedTag
  val `a11y-announcer` : TypedTag[Bldr, Output, FragT]
  val `a11y-keys` : TypedTag[Bldr, Output, FragT]
  val `a11y-keys-behavior` : TypedTag[Bldr, Output, FragT]
  val `ajax` : TypedTag[Bldr, Output, FragT]
  val `autogrow-textarea` : TypedTag[Bldr, Output, FragT]
  val `behaviors` : TypedTag[Bldr, Output, FragT]
  val `checked-element-behavior` : TypedTag[Bldr, Output, FragT]
  val `collapse` : TypedTag[Bldr, Output, FragT]
  val `component-page` : TypedTag[Bldr, Output, FragT]
  val `demo-helpers` : TypedTag[Bldr, Output, FragT]
  val `doc-viewer` : TypedTag[Bldr, Output, FragT]
  val `dropdown` : TypedTag[Bldr, Output, FragT]
  val `fit-behavior` : TypedTag[Bldr, Output, FragT]
  val `flex-layout` : TypedTag[Bldr, Output, FragT]
  val `form` : TypedTag[Bldr, Output, FragT]
  val `form-element-behavior` : TypedTag[Bldr, Output, FragT]
  val `icon` : TypedTag[Bldr, Output, FragT]
  val `icons` : TypedTag[Bldr, Output, FragT]
  val `iconset` : TypedTag[Bldr, Output, FragT]
  val `iconset-svg` : TypedTag[Bldr, Output, FragT]
  val `image` : TypedTag[Bldr, Output, FragT]
  val `input` : TypedTag[Bldr, Output, FragT]
  val `jsonp-library` : TypedTag[Bldr, Output, FragT]
  val `label` : TypedTag[Bldr, Output, FragT]
  val `list` : TypedTag[Bldr, Output, FragT]
  val `localstorage` : TypedTag[Bldr, Output, FragT]
  val `media-query` : TypedTag[Bldr, Output, FragT]
  val `menu-behavior` : TypedTag[Bldr, Output, FragT]
  val `meta` : TypedTag[Bldr, Output, FragT]
  val `overlay-behavior` : TypedTag[Bldr, Output, FragT]
  val `page-url` : TypedTag[Bldr, Output, FragT]
  val `pages` : TypedTag[Bldr, Output, FragT]
  val `range-behavior` : TypedTag[Bldr, Output, FragT]
  val `resizable-behavior` : TypedTag[Bldr, Output, FragT]
  val `selector` : TypedTag[Bldr, Output, FragT]
  val `signals` : TypedTag[Bldr, Output, FragT]
  val `swipeable-container` : TypedTag[Bldr, Output, FragT]
  val `test-helpers` : TypedTag[Bldr, Output, FragT]
  val `validatable-behavior` : TypedTag[Bldr, Output, FragT]
  val `validator-behavior` : TypedTag[Bldr, Output, FragT]
}

/** Polymer Iron Elements
  *  A set of visual and non-visual utility elements. Includes elements for working with layout, user input,
  *  selection, and scaffolding apps.
  *
  */
trait PolymerIronTags extends GenericPolymerIronTags[Builder, String, String] {
  /** A singleton element that simplifies announcing text to screen readers.*/
  lazy val `a11y-announcer` = "iron-ally-announcer".tag

  /** A basic element implementation of iron-a11y-keys-behavior, matching the legacy core-a11y-keys.*/
  lazy val `a11y-keys` = "iron-a11y-keys".tag

  /** A behavior that enables keybindings for greater a11y.*/
  lazy val `a11y-keys-behavior` = "iron-a11y-keys-behavior".tag

  /**  Makes it easy to make ajax calls and parse the response*/
  lazy val `ajax` = "iron-ajax".tag

  /** A textarea element that automatically grows with input*/
  lazy val `autogrow-textarea` = "iron-autogrow-textarea".tag

  /** Provides a set of behaviors for the iron elements*/
  lazy val `behaviors` = "iron-behaviors".tag

  /** Implements an element that has a checked attribute and can be added to a form*/
  lazy val `checked-element-behavior` = "iron-checked-element-behavior".tag

  /** Provides a collapsable container*/
  lazy val `collapse` = "iron-collapse".tag

  /** Turns a raw element definition into beautiful documentation*/
  lazy val `component-page` = "iron-component-page".tag

  /** Utility classes to make building demo pages easier*/
  lazy val `demo-helpers` = "iron-demo-helpers".tag

  /** Elements for rendering Polymer component documentation.*/
  lazy val `doc-viewer` = "iron-doc-viewer".tag

  /** An unstyled element that works similarly to a native browser select*/
  lazy val `dropdown` = "iron-dropdown".tag

  /** Fits an element inside another element*/
  lazy val `fit-behavior` = "iron-fit-behavior".tag

  /** Provide flexbox-based layouts*/
  lazy val `flex-layout` = "iron-flex-layout".tag

  /** Makes it easier to manage forms*/
  lazy val `form` = "iron-form".tag

  /** Enables a custom element to be included in an iron-form*/
  lazy val `form-element-behavior` = "iron-form-element-behavior".tag

  /** An element that supports displaying an icon*/
  lazy val `icon` = "iron-icon".tag

  /** A set of icons for use with iron-icon*/
  lazy val `icons` = "iron-icons".tag

  /** Represents a set of icons*/
  lazy val `iconset` = "iron-iconset".tag

  /** Manages a set of svg icons*/
  lazy val `iconset-svg` = "iron-iconset-svg".tag

  /** An image-displaying element with lots of convenient features*/
  lazy val `image` = "iron-image".tag

  /**An input element with data binding*/
  lazy val `input` = "iron-input".tag

  /** Loads jsonp libraries*/
  lazy val `jsonp-library` = "iron-jsonp-library".tag

  /** A version of the label element that works with custom elements as well as native elements*/
  lazy val `label` = "iron-label".tag

  /** Displays a virtual, 'infinite' scrolling list of items*/
  lazy val `list` = "iron-list".tag

  /** Provides access to local storage*/
  lazy val `localstorage` = "iron-localstorage".tag

  /** Lets you bind to a CSS media query*/
  lazy val `media-query` = "iron-media-query".tag

  /** Provides accessible menu behavior*/
  lazy val `menu-behavior` = "iron-menu-behavior".tag

  /** Useful for sharing information across a DOM tree*/
  lazy val `meta` = "iron-meta".tag

  /** Provides a behavior for making an element an overlay*/
  lazy val `overlay-behavior` = "iron-overlay-behavior".tag

  /** Bidirectional data binding into the page's URL.*/
  lazy val `page-url` = "iron-page-url".tag

  /** Organizes a set of pages and shows one at a time*/
  lazy val `pages` = "iron-pages".tag

  /** Provides a behavior for something with a minimum and maximum value*/
  lazy val `range-behavior` = "iron-range-behavior".tag

  /** Coordinates the flow of resizeable elements*/
  lazy val `resizable-behavior` = "iron-resizable-behavior".tag

  /** Manages a set of elements that can be selected*/
  lazy val `selector` = "iron-selector".tag

  /** Basic publish-subscribe functionality*/
  lazy val `signals` = "iron-signals".tag

  /** A container that alows any of its nested children to be swiped away.*/
  lazy val `swipeable-container` = "iron-swipeable-container".tag

  /** Utility classes to help make testing easier*/
  lazy val `test-helpers` = "iron-test-helpers".tag

  /** Provides a behavior for an element that validates user input*/
  lazy val `validatable-behavior` = "iron-validatable-behavior".tag

  /** Implements a input validator*/
  lazy val `validator-behavior` = "iron-validator-behavior".tag
}


trait GenericPolymerPaperTags[Bldr, Output <: FragT, FragT] extends Util[Bldr, Output, FragT] {
  import scalatags.generic.TypedTag
  val `badge` : TypedTag[Bldr, Output, FragT]
  val `behaviors` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `button` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `card` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `checkbox` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `dialog` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `dialog-behavior` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `dialog-scrollable` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `drawer-panel` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `dropdown-menu` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `fab` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `header-panel` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `icon-button` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `input` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `item` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `listbox` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `material` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `menu` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `menu-button` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `progress` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `radio-button` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `radio-group` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `ripple` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `scroll-header-panel` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `slider` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `spinner` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `styles` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `tabs` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `toast` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `toggle-button` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `toolbar` : scalatags.generic.TypedTag[Bldr, Output, FragT]
  val `tooltip` : scalatags.generic.TypedTag[Bldr, Output, FragT]
}

/** Polymer Paper Elements
  * Paper elements are a set of visual elements that implement Google's Material Design.
  */
trait PolymerPaperTags extends GenericPolymerPaperTags[Builder, String, String] {
  /** Material design status message for elements */
  lazy val `badge` = "paper-badge".tag
  /** Common behaviors across the paper elements */
  lazy val `behaviors` = "paper-behaviors".tag
  /** Material design button */
  lazy val `button` = "paper-button".tag
  /** Material design piece of paper with unique related data */
  lazy val `card` = "paper-card".tag
  /** A material design checkbox */
  lazy val `checkbox` = "paper-checkbox".tag
  /** A Material Design dialog */
  lazy val `dialog` = "paper-dialog".tag
  /** Implements a behavior used for material design dialogs */
  lazy val `dialog-behavior` = "paper-dialog-behavior".tag
  /** A scrollable area used inside the material design dialog */
  lazy val `dialog-scrollable` = "paper-dialog-scrollable".tag
  /** A responsive drawer panel */
  lazy val `drawer-panel` = "paper-drawer-panel".tag
  /** An element that works similarly to a native browser select */
  lazy val `dropdown-menu` = "paper-dropdown-menu".tag
  /** A material design floating action button */
  lazy val `fab` = "paper-fab".tag
  /** A header and content wrapper for layout with headers */
  lazy val `header-panel` = "paper-header-panel".tag
  /** A material design icon button */
  lazy val `icon-button` = "paper-icon-button".tag
  /** Material design text fields */
  lazy val `input` = "paper-input".tag
  /** A material-design styled list item */
  lazy val `item` = "paper-item".tag
  /** Implements an accessible material design listbox */
  lazy val `listbox` = "paper-listbox".tag
  /** A material design container that looks like a lifted sheet of paper */
  lazy val `material` = "paper-material".tag
  /** Implements an accessible material design menu */
  lazy val `menu` = "paper-menu".tag
  /** A material design element that composes a trigger and a dropdown menu */
  lazy val `menu-button` = "paper-menu-button".tag
  /** A material design progress bar */
  lazy val `progress` = "paper-progress".tag
  /** A material design radio button */
  lazy val `radio-button` = "paper-radio-button".tag
  /** A group of material design radio buttons */
  lazy val `radio-group` = "paper-radio-group".tag
  /** Adds a material design ripple to any container */
  lazy val `ripple` = "paper-ripple".tag
  /** A header bar with scrolling behavior */
  lazy val `scroll-header-panel` = "paper-scroll-header-panel".tag
  /** A material design-style slider */
  lazy val `slider` = "paper-slider".tag
  /** A material design spinner */
  lazy val `spinner` = "paper-spinner".tag
  /** Common (global) styles for Material Design elements. */
  lazy val `styles` = "paper-styles".tag
  /** Material design tabs */
  lazy val `tabs` = "paper-tabs".tag
  /** A material design notification toast */
  lazy val `toast` = "paper-toast".tag
  /** A material design toggle button control */
  lazy val `toggle-button` = "paper-toggle-button".tag
  /** A material design toolbar that is easily customizable */
  lazy val `toolbar` = "paper-toolbar".tag
  /** Material design tooltip popup for content */
  lazy val `tooltip` = "paper-tooltip".tag

}

trait PolymerAttributes  extends Util[Builder, String, String] {
  lazy val unresolved = "unresolved".attr := ""
}

object polymer {

  object text {

    object attrs extends Text.Cap with PolymerAttributes

    object iron extends Text.Cap with PolymerIronTags

    object paper extends Text.Cap with PolymerPaperTags

  }

}

trait PolymerLayout extends DetailedPageLayout {

  def arrangementDescription = Map(
    "content" → "The main content area for the page, 2/3 of the width",
    "endscripts" → "Scripts for the bottom of the page"
  )

  override def otherMeta(args : Arguments) = {
    Map(
      "theme-color" → "#2E3AA1", // Chrome for Android theme color
      "msapplication-TileColor" → "#3372DF", // Win8 tile color
      "application-name" → application(args).getOrElse("Polymer"),
      "mobile-web-app-capable" → "yes",
      "apple-mobile-web-app-capable" -> "yes",
      "apple-mobile-web-app-status-bar-style" -> "black",
      "apple-mobile-web-app-title" → application(args).getOrElse("Polymer"),
      "msapplication-TileImage" → "images/touch/ms-touch-icon-144x144-precomposed.png"
    )
  }

  override def otherLinks(args : Arguments) = {
    Seq(
      LinkTag("apple-touch-icon", "images/touch/apple-touch-icon.png", "image/png"),
      LinkTag("manifest", "manifest.json")
    )
  }

  override def imports(args : Arguments) = {
    super.imports(args) ++ Seq("elements/elements.html")
  }

  override def javascriptLinks(args : Arguments) = {
    super.javascriptLinks(args) ++ Seq("bower_components/webcomponentsjs/webcomponents-lite.js")

  }

  import scalatags.Text.all._

  override def bodyTag(args : Arguments) = {
    import polymer.text.attrs._
    val params : Seq[Modifier] = Seq(unresolved) ++ args.content.getOrElse("content", emptyContents)
    body(params:_*)
  }
}
