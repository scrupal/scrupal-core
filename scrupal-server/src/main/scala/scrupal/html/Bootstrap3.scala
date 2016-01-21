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
  def arrangementDescription = Map(
    "nav" → "Navigation bar and header",
    "content" → "The main content area for the page, 2/3 of the width",
    "endscripts" → "Scripts for the bottom of the page"
  )

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

  override def bodyTag(args: Arguments) : HtmlElement = {
    body(
      tags2.nav(`class`:="navbar navbar-inverse navbar-fixed-top", args.content.get("nav")),
      div(`class`:="container-fluid", args.content.get("content")),
      args.content.get("endscripts")
    )
  }
}

class SimpleBootstrapLayout(implicit val scrupal : Scrupal) extends BootstrapLayout {
  def id : Identifier = 'SimpleBootstrapLayout
  val description: String = "A simple Twitter Bootstrap layout with navigation bar, fluid container, and end scripts"
}

class ThreeColumnBootstrapLayout(implicit val scrupal : Scrupal) extends BootstrapLayout {
  def id : Identifier = 'ThreeColumnBootstrapLayout
  val description: String =
    "A Bootstrap 3 page in three columns with havigation bar, header, fluid centered content, " +
      "left and right sidebars, footer and end scripts"

  override def arrangementDescription = super.arrangementDescription ++ Map(
    "header" → "A centered, full width, header section to describe the main content",
    "left" → "A left side bar 1/6th of the width",
    "right" → "A right side bar 1/6th of the width",
    "footer" → "A footer area below the content and side bars"
  )

  override def bodyTag(args: Arguments) : HtmlElement = {
    body(
      tags2.nav(`class`:="navbar navbar-inverse navbar-fixed-top", args.content.get("nav")),
      div(`class`:="container-fluid",
        args.content.get("header"),
        div(`class`:="row",
          div(`class`:="col-sm-2", args.content.get("left")),
          div(`class`:="col-sm-8",
            div(`class`:="container-fluid", args.content.get("content"))
          ),
          div(`class`:="col-sm-2", args.content.get("right"))
        ),
        hr(),
        footer( args.content.get("footer") )
      ),
      args.content.get("endscripts")
    )
  }
}
