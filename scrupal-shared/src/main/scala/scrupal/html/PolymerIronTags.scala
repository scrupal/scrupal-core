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

import scalatags.generic.Util

/** Polymer Iron Tags
  *  A set of visual and non-visual utility elements. Includes elements for working with layout, user input,
  *  selection, and scaffolding apps.
  */
trait PolymerIronTags[Bldr, Output <: FragT, FragT] extends Util[Bldr, Output, FragT] {
  /** A singleton element that simplifies announcing text to screen readers. */
  lazy val `a11y-announcer` = "iron-ally-announcer".tag

  /** A basic element implementation of iron-a11y-keys-behavior, matching the legacy core-a11y-keys. */
  lazy val `a11y-keys` = "iron-a11y-keys".tag

  /** A behavior that enables keybindings for greater a11y. */
  lazy val `a11y-keys-behavior` = "iron-a11y-keys-behavior".tag

  /** Makes it easy to make ajax calls and parse the response */
  lazy val `ajax` = "iron-ajax".tag

  /** A textarea element that automatically grows with input */
  lazy val `autogrow-textarea` = "iron-autogrow-textarea".tag

  /** Provides a set of behaviors for the iron elements */
  lazy val `behaviors` = "iron-behaviors".tag

  /** Implements an element that has a checked attribute and can be added to a form */
  lazy val `checked-element-behavior` = "iron-checked-element-behavior".tag

  /** Provides a collapsable container */
  lazy val `collapse` = "iron-collapse".tag

  /** Turns a raw element definition into beautiful documentation */
  lazy val `component-page` = "iron-component-page".tag

  /** Utility classes to make building demo pages easier */
  lazy val `demo-helpers` = "iron-demo-helpers".tag

  /** Elements for rendering Polymer component documentation. */
  lazy val `doc-viewer` = "iron-doc-viewer".tag

  /** An unstyled element that works similarly to a native browser select */
  lazy val `dropdown` = "iron-dropdown".tag

  /** Fits an element inside another element */
  lazy val `fit-behavior` = "iron-fit-behavior".tag

  /** Provide flexbox-based layouts */
  lazy val `flex-layout` = "iron-flex-layout".tag

  /** Makes it easier to manage forms */
  lazy val `form` = "iron-form".tag

  /** Enables a custom element to be included in an iron-form */
  lazy val `form-element-behavior` = "iron-form-element-behavior".tag

  /** An element that supports displaying an icon */
  lazy val `icon` = "iron-icon".tag

  /** A set of icons for use with iron-icon */
  lazy val `icons` = "iron-icons".tag

  /** Represents a set of icons */
  lazy val `iconset` = "iron-iconset".tag

  /** Manages a set of svg icons */
  lazy val `iconset-svg` = "iron-iconset-svg".tag

  /** An image-displaying element with lots of convenient features */
  lazy val `image` = "iron-image".tag

  /** An input element with data binding */
  lazy val `input` = "iron-input".tag

  /** Loads jsonp libraries */
  lazy val `jsonp-library` = "iron-jsonp-library".tag

  /** A version of the label element that works with custom elements as well as native elements */
  lazy val `label` = "iron-label".tag

  /** Displays a virtual, 'infinite' scrolling list of items */
  lazy val `list` = "iron-list".tag

  /** Provides access to local storage */
  lazy val `localstorage` = "iron-localstorage".tag

  /** Lets you bind to a CSS media query */
  lazy val `media-query` = "iron-media-query".tag

  /** Provides accessible menu behavior */
  lazy val `menu-behavior` = "iron-menu-behavior".tag

  /** Useful for sharing information across a DOM tree */
  lazy val `meta` = "iron-meta".tag

  /** Provides a behavior for making an element an overlay */
  lazy val `overlay-behavior` = "iron-overlay-behavior".tag

  /** Bidirectional data binding into the page's URL. */
  lazy val `page-url` = "iron-page-url".tag

  /** Organizes a set of pages and shows one at a time */
  lazy val `pages` = "iron-pages".tag

  /** Provides a behavior for something with a minimum and maximum value */
  lazy val `range-behavior` = "iron-range-behavior".tag

  /** Coordinates the flow of resizeable elements */
  lazy val `resizable-behavior` = "iron-resizable-behavior".tag

  /** Manages a set of elements that can be selected */
  lazy val `selector` = "iron-selector".tag

  /** Basic publish-subscribe functionality */
  lazy val `signals` = "iron-signals".tag

  /** A container that alows any of its nested children to be swiped away. */
  lazy val `swipeable-container` = "iron-swipeable-container".tag

  /** Utility classes to help make testing easier */
  lazy val `test-helpers` = "iron-test-helpers".tag

  /** Provides a behavior for an element that validates user input */
  lazy val `validatable-behavior` = "iron-validatable-behavior".tag

  /** Implements a input validator */
  lazy val `validator-behavior` = "iron-validator-behavior".tag
}

trait PolymerIronAttributes[Bldr, Output <: FragT, FragT] extends Util[Bldr, Output, FragT] {
  lazy val unresolved = "unresolved".attr := ""

  /** activeRequests @see https://elements.polymer-project.org/elements/iron-ajax#property-activeRequests */
  lazy val activeRequests = "activeRequests".attr
  /** auto @see https://elements.polymer-project.org/elements/iron-ajax#property-auto */
  lazy val auto = "auto".attr
  /** body @see https://elements.polymer-project.org/elements/iron-ajax#property-body */
  lazy val body = "body".attr
  /** contentType @see https://elements.polymer-project.org/elements/iron-ajax#property-contentType */
  lazy val contentType = "body".attr
  /** debounceDuration @see https://elements.polymer-project.org/elements/iron-ajax#property-debounceDuration */
  lazy val debounceDuration = "debounceDuration".attr
  /** handleAs @see https://elements.polymer-project.org/elements/iron-ajax#property-handleAs */
  lazy val handleAs = "handleAs".attr
  /** headers @see https://elements.polymer-project.org/elements/iron-ajax#property-headers */
  lazy val headers = "headers".attr
  /** jsonPrefix @see https://elements.polymer-project.org/elements/iron-ajax#property-jsonPrefix */
  lazy val jsonPrefix = "jsonPrefix".attr
  /** lastError @see https://elements.polymer-project.org/elements/iron-ajax#property-lastError */
  lazy val lastError = "lastError".attr
  /** lastRequest @see https://elements.polymer-project.org/elements/iron-ajax#property-lastRequest */
  lazy val lastRequest = "lastRequest".attr
  /** lastResponse @see https://elements.polymer-project.org/elements/iron-ajax#property-lastResponse */
  lazy val lastResponse = "lastResponse".attr
  /** loading @see https://elements.polymer-project.org/elements/iron-ajax#property-loading */
  lazy val loading = "loading".attr
  /** method @see https://elements.polymer-project.org/elements/iron-ajax#property-method */
  lazy val method = "method".attr
  /** params @see https://elements.polymer-project.org/elements/iron-ajax#property-params */
  lazy val params = "params".attr
  /** queryString @see https://elements.polymer-project.org/elements/iron-ajax#property-queryString */
  lazy val queryString = "queryString".attr
  /** requestHeaders @see https://elements.polymer-project.org/elements/iron-ajax#property-requestHeaders */
  lazy val requestHeaders = "requestHeaders".attr
  /** requestUrl @see https://elements.polymer-project.org/elements/iron-ajax#property-requestUrl */
  lazy val requestUrl = "requestUrl".attr
  /** sync @see https://elements.polymer-project.org/elements/iron-ajax#property-sync */
  lazy val sync = "sync".attr
  /** timeout @see https://elements.polymer-project.org/elements/iron-ajax#property-timeout */
  lazy val timeout = "timeout".attr
  /** url @see https://elements.polymer-project.org/elements/iron-ajax#property-url */
  lazy val url = "url".attr
  /** verbose @see https://elements.polymer-project.org/elements/iron-ajax#property-verbose */
  lazy val verbose = "verbose".attr
  /** withCredentials @see https://elements.polymer-project.org/elements/iron-ajax#property-withCredentials */
  lazy val withCredentials = "withCredentials".attr
}
