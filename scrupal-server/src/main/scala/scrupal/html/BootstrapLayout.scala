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

import scalatags.Text.all._
import scalatags.Text.tags2

trait BootstrapLayout extends DetailedPageLayout {

  override def javascriptLinks(args: Arguments) : Seq[String] = {
    super.javascriptLinks(args) ++ Seq(
      assets.webjar("jquery", "jquery.min.js").url,
      assets.webjar("bootstrap", "js/bootstrap.min.js" ).url,
      assets.webjar("modernizr", "modernizr.min.js").url
    )
  }
  override def stylesheetLinks(args : Arguments) : Seq[String] = {
    super.stylesheetLinks(args) ++ Seq(
      assets.theme(args.context.themeName).url,
      assets.webjar("font-awesome", "css/font-awesome.min.css").url
    )
  }

  override def prologue(args: Arguments) : HtmlContents = {
    Seq(
      super.prologue(args),
      tags2.nav(`class`:="navbar navbar-inverse navbar-fixed-top", args.args.get("nav"))
    )
  }

  override def bodyTag(args: Arguments) : HtmlElement = {
    body(
      prologue(args),
      div(`class`:="container-fluid", contents(args)),
      epilogue(args)
    )
  }
}

class SimpleBootstrapLayout(implicit val scrupal : Scrupal) extends BootstrapLayout {
  def id : Identifier = 'SimpleBootstrapLayout
  val description: String = "A simple Twitter Bootstrap layout with navigation bar, fluid container, and end scripts"
}

trait ThreeColBootstrapLayout extends BootstrapLayout {
  override def argumentDescription = super.argumentDescription ++ Map(
    "left" → "A left side bar 1/6th of the width",
    "center" → "A center main content area",
    "right" → "A right side bar 1/6th of the width"
  )

  def left(args : Arguments) : HtmlContents = {
    val l : String = args.args.getOrElse("left", "")
    div(`class`:="col-sm-2", l)
  }

  def center(args : Arguments) : HtmlContents = {
    val c : String = args.args.getOrElse("center", "")
    div(`class`:="col-sm-8", c)
  }

  def right(args : Arguments) : HtmlContents = {
    val r : String = args.args.getOrElse("right", "")
    div(`class`:="col-sm-2", r)
  }

  override def contents(args: Arguments) : HtmlContents = {
    div(`class`:="row",left(args),center(args),right(args))
  }
}

class ThreeColumnBootstrapLayout(implicit val scrupal : Scrupal) extends ThreeColBootstrapLayout {
  def id: Identifier = 'ThreeColumnBootstrapLayout

  val description: String =
    "A Bootstrap 3 page in three columns with havigation bar, header, fluid centered content, " +
      "left and right sidebars, footer and end scripts"
}

