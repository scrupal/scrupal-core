package scrupal.html

import scalatags.generic.Util

/** Polymer Paper Elements
  * Paper elements are a set of visual elements that implement Google's Material Design.
  */
trait PolymerPaperTags[Builder, Output <: FragT, FragT] extends Util[Builder, Output, FragT] {
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
