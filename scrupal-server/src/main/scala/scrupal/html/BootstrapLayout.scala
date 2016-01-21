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
  override def arrangementDescription = super.arrangementDescription ++ Map(
    "nav" → "Navigation bar and header"
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

  override def header(args: Arguments) : HtmlContents = {
    Seq(
      tags2.nav(`class`:="navbar navbar-inverse navbar-fixed-top", args.content.get("nav")),
      super.header(args)
    )
  }

  override def bodyTag(args: Arguments) : HtmlElement = {
    body(
      header(args),
      div(`class`:="container-fluid", contents(args), footer(args)),
      endScripts(args)
    )
  }
}

class SimpleBootstrapLayout(implicit val scrupal : Scrupal) extends BootstrapLayout {
  def id : Identifier = 'SimpleBootstrapLayout
  val description: String = "A simple Twitter Bootstrap layout with navigation bar, fluid container, and end scripts"
}

trait ThreeColBootstrapLayout extends BootstrapLayout {
  override def arrangementDescription = super.arrangementDescription ++ Map(
    "left" → "A left side bar 1/6th of the width",
    "right" → "A right side bar 1/6th of the width"
  )

  override def contents(args: Arguments) : HtmlContents = {
    val left : Seq[HtmlFragment] = args.content.getOrElse("left",emptyContents)
    val cont : Seq[HtmlFragment] = args.content.getOrElse("contents", emptyContents)
    val right : Seq[HtmlFragment] = args.content.getOrElse("right",emptyContents)
    Seq(
      div(`class`:="row",
        div(`class`:="col-sm-2", left),
        div(`class`:="col-sm-8", div(`class`:="container-fluid", cont)),
        div(`class`:="col-sm-2", right)
      ),
      hr()
    )
  }
}

class ThreeColumnBootstrapLayout(implicit val scrupal : Scrupal) extends ThreeColBootstrapLayout {
  def id: Identifier = 'ThreeColumnBootstrapLayout

  val description: String =
    "A Bootstrap 3 page in three columns with havigation bar, header, fluid centered content, " +
      "left and right sidebars, footer and end scripts"
}

