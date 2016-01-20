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

trait GenericPolymerIronTags[Builder, Output <: FragT, FragT] extends Util[Builder, Output, FragT] {
  val `iron-a11y-announcer` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-a11y-keys` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-a11y-keys-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-ajax` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-autogrow-textarea` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-behaviors` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-checked-element-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-collapse` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-component-page` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-demo-helpers` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-doc-viewer` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-dropdown` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-fit-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-flex-layout` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-form` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-form-element-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-icon` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-icons` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-iconset` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-iconset-svg` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-image` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-input` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-jsonp-library` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-label` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-list` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-localstorage` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-media-query` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-menu-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-meta` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-overlay-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-page-url` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-pages` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-range-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-resizable-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-selector` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-signals` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-swipeable-container` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-test-helpers` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-validatable-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `iron-validator-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
}

/** Polymer Iron Elements
  *  A set of visual and non-visual utility elements. Includes elements for working with layout, user input,
  *  selection, and scaffolding apps.
  *
  */
trait PolymerIronTags extends GenericPolymerIronTags[Builder, String, String] {
  /** A singleton element that simplifies announcing text to screen readers.*/
  lazy val `iron-a11y-announcer` = "iron-ally-announcer".tag

  /** A basic element implementation of iron-a11y-keys-behavior, matching the legacy core-a11y-keys.*/
  lazy val `iron-a11y-keys` = "iron-a11y-keys".tag

  /** A behavior that enables keybindings for greater a11y.*/
  lazy val `iron-a11y-keys-behavior` = "iron-a11y-keys-behavior".tag

  /**  Makes it easy to make ajax calls and parse the response*/
  lazy val `iron-ajax` = "iron-ajax".tag

  /** A textarea element that automatically grows with input*/
  lazy val `iron-autogrow-textarea` = "iron-autogrow-textarea".tag

  /** Provides a set of behaviors for the iron elements*/
  lazy val `iron-behaviors` = "iron-behaviors".tag

  /** Implements an element that has a checked attribute and can be added to a form*/
  lazy val `iron-checked-element-behavior` = "iron-checked-element-behavior".tag

  /** Provides a collapsable container*/
  lazy val `iron-collapse` = "iron-collapse".tag

  /** Turns a raw element definition into beautiful documentation*/
  lazy val `iron-component-page` = "iron-component-page".tag

  /** Utility classes to make building demo pages easier*/
  lazy val `iron-demo-helpers` = "iron-demo-helpers".tag

  /** Elements for rendering Polymer component documentation.*/
  lazy val `iron-doc-viewer` = "iron-doc-viewer".tag

  /** An unstyled element that works similarly to a native browser select*/
  lazy val `iron-dropdown` = "iron-dropdown".tag

  /** Fits an element inside another element*/
  lazy val `iron-fit-behavior` = "iron-fit-behavior".tag

  /** Provide flexbox-based layouts*/
  lazy val `iron-flex-layout` = "iron-flex-layout".tag

  /** Makes it easier to manage forms*/
  lazy val `iron-form` = "iron-form".tag

  /** Enables a custom element to be included in an iron-form*/
  lazy val `iron-form-element-behavior` = "iron-form-element-behavior".tag

  /** An element that supports displaying an icon*/
  lazy val `iron-icon` = "iron-icon".tag

  /** A set of icons for use with iron-icon*/
  lazy val `iron-icons` = "iron-icons".tag

  /** Represents a set of icons*/
  lazy val `iron-iconset` = "iron-iconset".tag

  /** Manages a set of svg icons*/
  lazy val `iron-iconset-svg` = "iron-iconset-svg".tag

  /** An image-displaying element with lots of convenient features*/
  lazy val `iron-image` = "iron-image".tag

  /**An input element with data binding*/
  lazy val `iron-input` = "iron-input".tag

  /** Loads jsonp libraries*/
  lazy val `iron-jsonp-library` = "iron-jsonp-library".tag

  /** A version of the label element that works with custom elements as well as native elements*/
  lazy val `iron-label` = "iron-label".tag

  /** Displays a virtual, 'infinite' scrolling list of items*/
  lazy val `iron-list` = "iron-list".tag

  /** Provides access to local storage*/
  lazy val `iron-localstorage` = "iron-localstorage".tag

  /** Lets you bind to a CSS media query*/
  lazy val `iron-media-query` = "iron-media-query".tag

  /** Provides accessible menu behavior*/
  lazy val `iron-menu-behavior` = "iron-menu-behavior".tag

  /** Useful for sharing information across a DOM tree*/
  lazy val `iron-meta` = "iron-meta".tag

  /** Provides a behavior for making an element an overlay*/
  lazy val `iron-overlay-behavior` = "iron-overlay-behavior".tag

  /** Bidirectional data binding into the page's URL.*/
  lazy val `iron-page-url` = "iron-page-url".tag

  /** Organizes a set of pages and shows one at a time*/
  lazy val `iron-pages` = "iron-pages".tag

  /** Provides a behavior for something with a minimum and maximum value*/
  lazy val `iron-range-behavior` = "iron-range-behavior".tag

  /** Coordinates the flow of resizeable elements*/
  lazy val `iron-resizable-behavior` = "iron-resizable-behavior".tag

  /** Manages a set of elements that can be selected*/
  lazy val `iron-selector` = "iron-selector".tag

  /** Basic publish-subscribe functionality*/
  lazy val `iron-signals` = "iron-signals".tag

  /** A container that alows any of its nested children to be swiped away.*/
  lazy val `iron-swipeable-container` = "iron-swipeable-container".tag

  /** Utility classes to help make testing easier*/
  lazy val `iron-test-helpers` = "iron-test-helpers".tag

  /** Provides a behavior for an element that validates user input*/
  lazy val `iron-validatable-behavior` = "iron-validatable-behavior".tag

  /** Implements a input validator*/
  lazy val `iron-validator-behavior` = "iron-validator-behavior".tag
}


trait GenericPolymerPaperTags[Builder, Output <: FragT, FragT] extends Util[Builder, Output, FragT] {
  import scalatags.generic.TypedTag
  val `paper-badge` : TypedTag[Builder, Output, FragT]
  val `paper-behaviors` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-button` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-card` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-checkbox` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-dialog` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-dialog-behavior` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-dialog-scrollable` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-drawer-panel` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-dropdown-menu` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-fab` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-header-panel` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-icon-button` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-input` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-item` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-listbox` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-material` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-menu` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-menu-button` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-progress` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-radio-button` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-radio-group` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-ripple` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-scroll-header-panel` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-slider` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-spinner` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-styles` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-tabs` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-toast` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-toggle-button` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-toolbar` : scalatags.generic.TypedTag[Builder, Output, FragT]
  val `paper-tooltip` : scalatags.generic.TypedTag[Builder, Output, FragT]
}

/** Polymer Paper Elements
  * Paper elements are a set of visual elements that implement Google's Material Design.
  */
trait PolymerPaperTags extends GenericPolymerPaperTags[Builder, String, String] {
  /** Material design status message for elements */
  lazy val `paper-badge` = "paper-badge".tag
  /** Common behaviors across the paper elements */
  lazy val `paper-behaviors` = "paper-behaviors".tag
  /** Material design button */
  lazy val `paper-button` = "paper-button".tag
  /** Material design piece of paper with unique related data */
  lazy val `paper-card` = "paper-card".tag
  /** A material design checkbox */
  lazy val `paper-checkbox` = "paper-checkbox".tag
  /** A Material Design dialog */
  lazy val `paper-dialog` = "paper-dialog".tag
  /** Implements a behavior used for material design dialogs */
  lazy val `paper-dialog-behavior` = "paper-dialog-behavior".tag
  /** A scrollable area used inside the material design dialog */
  lazy val `paper-dialog-scrollable` = "paper-dialog-scrollable".tag
  /** A responsive drawer panel */
  lazy val `paper-drawer-panel` = "paper-drawer-panel".tag
  /** An element that works similarly to a native browser select */
  lazy val `paper-dropdown-menu` = "paper-dropdown-menu".tag
  /** A material design floating action button */
  lazy val `paper-fab` = "paper-fab".tag
  /** A header and content wrapper for layout with headers */
  lazy val `paper-header-panel` = "paper-header-panel".tag
  /** A material design icon button */
  lazy val `paper-icon-button` = "paper-icon-button".tag
  /** Material design text fields */
  lazy val `paper-input` = "paper-input".tag
  /** A material-design styled list item */
  lazy val `paper-item` = "paper-item".tag
  /** Implements an accessible material design listbox */
  lazy val `paper-listbox` = "paper-listbox".tag
  /** A material design container that looks like a lifted sheet of paper */
  lazy val `paper-material` = "paper-material".tag
  /** Implements an accessible material design menu */
  lazy val `paper-menu` = "paper-menu".tag
  /** A material design element that composes a trigger and a dropdown menu */
  lazy val `paper-menu-button` = "paper-menu-button".tag
  /** A material design progress bar */
  lazy val `paper-progress` = "paper-progress".tag
  /** A material design radio button */
  lazy val `paper-radio-button` = "paper-radio-button".tag
  /** A group of material design radio buttons */
  lazy val `paper-radio-group` = "paper-radio-group".tag
  /** Adds a material design ripple to any container */
  lazy val `paper-ripple` = "paper-ripple".tag
  /** A header bar with scrolling behavior */
  lazy val `paper-scroll-header-panel` = "paper-scroll-header-panel".tag
  /** A material design-style slider */
  lazy val `paper-slider` = "paper-slider".tag
  /** A material design spinner */
  lazy val `paper-spinner` = "paper-spinner".tag
  /** Common (global) styles for Material Design elements. */
  lazy val `paper-styles` = "paper-styles".tag
  /** Material design tabs */
  lazy val `paper-tabs` = "paper-tabs".tag
  /** A material design notification toast */
  lazy val `paper-toast` = "paper-toast".tag
  /** A material design toggle button control */
  lazy val `paper-toggle-button` = "paper-toggle-button".tag
  /** A material design toolbar that is easily customizable */
  lazy val `paper-toolbar` = "paper-toolbar".tag
  /** Material design tooltip popup for content */
  lazy val `paper-tooltip` = "paper-tooltip".tag

}

trait PolymerAttributes  extends Util[Builder, String, String] {
  lazy val unresolved = "unresolved".attr := ""
}

object polymer extends Text.Cap with PolymerIronTags with PolymerPaperTags with PolymerAttributes

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
  import polymer._

  override def bodyTag(args : Arguments) = {
    val params : Seq[Modifier] = Seq(unresolved) ++ args.content.getOrElse("content", emptyContents)
    body(params:_*)
  }
}
