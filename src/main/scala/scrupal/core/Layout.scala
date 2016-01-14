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

package scrupal.core

import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.MediaType
import com.reactific.helpers.{Registry, Registrable}
import play.twirl.api.{BufferedContent, Txt, Html}
import scrupal.utils.ScrupalComponent

import scala.concurrent.Future

/** Arranger Function.
  *
  * An arranger is a function that does the essential layout arrangement for a Layout. It takes in a Context and a
  * tag mapping and produces an `Array[Byte]` result. The Layout trait extends this function so that its apply method
  * can be used to perform the arranging.
  *
  */
trait Arranger[CT <: Content[_], FT <: BufferedContent[_]] extends ((Context, Layout.Arrangement[CT]) ⇒ Future[FT])
trait HtmlArranger extends Arranger[HtmlContent,Html]
trait TxtArranger extends Arranger[TextContent,Txt]
trait JsonArranger extends Arranger[JsonContent,Txt]

/** Abstract Layout
  *
  * A layout is a memory only function object. It
  */
trait Layout[CT <: Content[_], FT <: BufferedContent[_]]
  extends ScrupalComponent with Registrable[Layout[_,_]] with Arranger[CT,FT] {
  def registry = Layout
  def description : String
  def arrangementDescription: Map[String,String]
  def mediaType : MediaType
  def validateArgs(args : Layout.Arrangement[CT]) : Iterable[Throwable] = {
    for ((key,value) ← arrangementDescription if !args.contains(key)) yield {
      new IllegalArgumentException(s"Arrangement key '$key' is missing")
    }
  }
}

object Layout extends Registry[Layout[_,_]] {
  val registryName = "Layouts"
  val registrantsName = "layout"
  type Arrangement[CT <: Content[_]] = Map[String,CT]
}

trait HtmlLayout extends Layout[HtmlContent,Html] {
  final val mediaType = MediaTypes.`text/html`
}

trait TextLayout extends Layout[TextContent,Txt] {
  final val mediaType = MediaTypes.`text/plain`
}

trait JsonLayout extends Layout[JsonContent,Txt] {
  final val mediaType = MediaTypes.`application/json`
}

case object DefaultHtmlLayout extends HtmlLayout {
  def id: Identifier = 'DefaultHtmlLayout
  val description: String = "Default layout page used when the expected layout could not be found"
  def arrangementDescription = Map(
    "arguments" → "This template accepts all HtmlContent arguments"
  )
  def apply(context: Context, arrangement: Layout.Arrangement[HtmlContent]) : Future[Html] = {
    Future.successful { layout.html.defaultHtml(context, arrangement) }
  }
}

case object DefaultTextLayout extends TextLayout {
  def id: Identifier = 'DefaultTextLayout
  val description: String = "Default layout text used when the expected layout could not be found"
  def arrangementDescription = Map(
    "arguments" → "This template accepts all TextContent arguments"
  )
  def apply(context: Context, args : Layout.Arrangement[TextContent]) : Future[Txt] = {
    Future.successful { layout.txt.defaultText(context, args) }
  }
}

case object DefaultJsonLayout extends JsonLayout {
  def id : Identifier = 'DefaultJsonLayout
  val description : String = "Default layout for JSON that is used when the expected layout could not be found"
  def arrangementDescription = Map(
    "arguments" → "This template accepts all JsonContent arguments"
  )
  def apply(context : Context, args : Layout.Arrangement[JsonContent]) : Future[Txt] = {
    Future.successful { layout.txt.defaultJson(context, args)}
  }
}

case object StandardThreeColumnLayout extends HtmlLayout {
  def id : Identifier = 'StandardThreeColumnLayout
  val description: String = "A Bootstrap 3 page in three columns with Header, Navigation and Footer"
  def arrangementDescription = Map(
    "navheader" → "Navigation header",
    "navbar" → "Navigation bar content: a full width strip across the top of every page",
    "header" → "A centered, full width, header section to describe the main content",
    "left" → "A left side bar 1/6th of the width",
    "right" → "A right side bar 1/6th of the width",
    "content" → "The main content area for the page, 2/3 of the width",
    "footer" → "A footer area below the content and side bars"
  )
  def apply(context: Context, args : Layout.Arrangement[HtmlContent]) : Future[Html] = {
    validateArgs(args).map { arg ⇒ throw arg }
    Future.successful {
      val cpht = context.pageHeadTags
      val assets = router.scrupal.routes.Assets
      val links = cpht.links.copy(
        scriptLinks = cpht.links.scriptLinks ++ Seq(
          assets.webjar("jquery", "jquery.min.js").url,
          assets.webjar("bootstrap", "js/bootstrap.min.js" ).url,
          assets.webjar("marked", "marked.js").url,
          assets.webjar("modernizr", "modernizr.min.js").url
        ),
        stylesheets = cpht.links.stylesheets ++ Seq(
          assets.theme(context.themeName).url,
          assets.webjar("font-awesome", "css/font-awesome.min.css").url,
          assets.css("scrupal").url
        )
      )
      val pht = PageHeadTags(
        title = cpht.title,
        base = cpht.base,
        meta = cpht.meta,
        links = links,
        style = cpht.style,
        javascript = cpht.javascript
      )
      layout.html.standardThreeColumn(context, pht, args)
    }
  }
}

