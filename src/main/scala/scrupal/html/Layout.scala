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

import com.reactific.helpers.{Registrable, Registry}
import scrupal.core.{Scrupal, Identifier, Context}
import scrupal.utils.ScrupalComponent

import scala.concurrent.Future

import scalatags.Text.all._
import scalatags.generic.Namespace


/** Abstract Layout
  *
  * A layout is a memory only function object. It
  */
trait Layout extends ScrupalComponent with Registrable[Layout] with Arranger {
  type NamedContent  = Map[String,HtmlContents]

  implicit def scrupal : Scrupal

  case class Arguments(context : Context, content : NamedContent)

  def registry : LayoutRegistry = scrupal.layouts

  def description : String
  def arrangementDescription: Map[String,String]
  def validateArrangement(args : Arrangement) : Iterable[Throwable] = {
    for ((key,value) ← arrangementDescription if !args.contains(key)) yield {
      new IllegalArgumentException(s"Content key '$key' is missing")
    }
  }
  def apply(context : Context, arrangement: Arrangement) : Future[HtmlElement] = Future {
    validateArrangement(arrangement).map { x ⇒ throw x }
    val content : NamedContent = for ( (name, gen) ← arrangement) yield { name → gen(context) }
    layout(Arguments(context,content))
  }(context.scrupal.executionContext)

  def layout(args : Arguments) : HtmlElement
}

case class LayoutRegistry() extends Registry[Layout] {
  val registryName = "Layouts"
  val registrantsName = "layout"
}

trait PageLayout extends Layout {

  /** Generate the page title
    * @param args the arguments with which to customize the title
    * @return the title of the page for inclusion in the meta tags
    */
  def pageTitle(args : Arguments) : String = "Scrupal"

  /** Generate HTML head Tag
    *
    * @param args Arguments from which the head tag should be derived
    * @return
    */
  def headTag(args: Arguments): HtmlElement = {
    head("title".tag[String](Namespace.htmlNamespaceConfig)(pageTitle(args)))
  }

  /** Generate HTML body Tag
    *
    * @param args Arguments from which the body tag should be derived
    * @return
    */
  def bodyTag(args : Arguments) : HtmlElement = {
    body(span(em("OOPS!"), " You forgot to override bodyTag(args:Arguments)!"))
  }

  /** Lay out the content of this page
    *
    * @param args Arguments from which this page's content should be derived
    * @return
    */
  def layout(args : Arguments) : HtmlElement = {
    html(headTag(args),bodyTag(args))
  }

  /** Get a full page for this layout with the DOCTYPE declaration (HTML5 only)
    *
    * @param context The context in which to run the page generation
    * @param arrangement The argument arrangement for substituting into the page layout
    * @return
    */
  def page(context : Context, arrangement: Arrangement) : Future[String]  = {
    apply(context, arrangement).map { contents ⇒ "<!DOCTYPE html>" + contents.render } (context.scrupal.executionContext)
  }
}

class DefaultPageLayout(implicit val scrupal : Scrupal) extends PageLayout {
  def id: Identifier = 'DefaultPageLayout
  val description: String = "Default page layout used when the expected layout could not be found"
  def arrangementDescription = Map(
    "arguments" → "This template accepts all HtmlContent arguments"
  )
  override def validateArrangement(args : Arrangement) = Iterable.empty[Throwable]
  override def bodyTag(args : Arguments) : HtmlElement = {
    val content = Seq(
      h1("Layout Missing"),
      p("You are seeing this page because the requested layout was not found. As a result you are seeing this",
        "basic default layout which just lists the content down the page. This probably isn't what you want, but it's",
        "what you've got until you specify the layout for your pages.")
    ) ++ args.content.flatMap { case (key,cont) ⇒ Seq(h2(key),div(cont:_*)) }
    body(content:_*)
  }
}

/** HTML Base Tag Representation
  * Represents the HTML {{{<base href="@htref" target="@target">}}} tag and converts to HTML content
 *
  * @param href The default url to use for links on the page
  * @param target The default link target ("_blank" or "_self" typically)
  */
case class BaseTag(href: String, target: String)

/** Representation For An Icon Link tag */
case class IconLinkTag(url: String, x: Int, y: Int, typ: String)

case class LinkTag(relation: String, reference: String, contentType : String = "text/html") {
  def render : HtmlElement = link(rel:=relation, `type`:=contentType, href:=reference)
}

trait DetailedPageLayout extends PageLayout {

  /** Generate HTML Head Element
    * Generates the head tag and all content it requires
 *
    * @param args the arguments with which to customize the head element
    * @return The head element
    */
  override def headTag(args: Arguments) : HtmlElement = {
    val contents : List[HtmlElement] = List(
      "title".tag[String](Namespace.htmlNamespaceConfig)(pageTitle(args)),
      meta(name:="viewport",content:="width=device-width, initial-scale=1.0")
    ) ++ List[Option[HtmlElement]](
      baseTag(args).map { tag ⇒ base(href:=tag.href,target:=tag.target) }
    ).flatten ++ metas(args) ++ links(args) ++
      sheets(args).map { s ⇒ "style".tag[String](Namespace.htmlNamespaceConfig)(s) } ++
      scripts(args).map { s ⇒ script(s) }
    head(contents)
  }

  /** HTML Base Tag
    * Represents the HTML {{{<base href="@htref" target="@target">}}} tag and provides the href and target as a result
 *
    * @return (href,target): href is the default url to use for links on the page, target is the default link target
    */
  def baseTag(args : Arguments) : Option[BaseTag] = None

  /** Generate HTML meta tags
 *
    * @param args The arguments with which to customize the meta tags
    * @return A List of the meta elements for the head
    */
  def metas(args : Arguments) : List[HtmlElement] = {
    val result = List[Option[HtmlElement]](
      Some(meta(charset:="UTF-8")),
      description(args).map { desc ⇒ meta(name:="description",content:=desc) },
      authorName(args).map { auth ⇒ meta(name:="author",content:=auth) },
      generator(args).map { gen ⇒ meta(name:="generator",content:=gen) },
      application(args).map { app ⇒ meta(name:="application",content:=app) }, {
        val kw = keywords(args).distinct.mkString(",")
        if (kw.nonEmpty) {Some(meta(name := "keywords", content := kw))} else None
      }, {
        val ref = refresh(args)
        if (ref > 0) { Some(meta(`http-equiv`:="refresh", content := ref)) } else None
      }) ++ otherMeta(args).map { case (nm,cont) ⇒ Some(meta(name:=nm,content:=cont)) }
    result.flatten
  }

  /** Generate Page Descriptions
 *
    * @param args Arguments from which the page description is derived
    * @return An optional string containing the page's description (for meta tag)
    */
  def description(args : Arguments) : Option[String] = None

  /** Generate Page Author
 *
    * @param args Arguments from which the page's author is derived
    * @return An optional string containing the page's author (for meta tag)
    */
  def authorName(args : Arguments) : Option[String] = None

  /** Generate Page Generator
 *
    * @param args Arguments from which the page's generator is derived
    * @return An optional string containing the page's generator name (for meta tag)
    */
  def generator(args : Arguments) : Option[String] = Some("Scrupal")

  /** Generate Page Application
 *
    * @param args Arguments from which the page's application is derived
    * @return An optional string containing the page's application name (for meta tag)
    */
  def application(args : Arguments) : Option[String] = None

  /** Generate Page Keywords
 *
    * @param args Arguments from which the page's keywords are derived
    * @return Strings that provide the keywords for the page (for meta tag)
    */
  def keywords(args : Arguments) : Seq[String] = Seq.empty[String]

  /** Generate Page Refresh Time
 *
    * @param args Arguments from which the page's refresh delay is derived
    * @return A non-zero value providing the number of seconds after which the page should automatically
    *         refresh or zero if that feature is not desired.
    */
  def refresh(args : Arguments) : Int = 0

  def otherMeta(args: Arguments) : Map[String,String] = Map.empty[String,String]

  /** Generate Page link tags
    *
    * @param args The argument which which to customize the link tags
    * @return A list of link tags for the head tag
    */
  def links(args: Arguments) : List[HtmlElement] = {
    val imps = imports(args).map { imp ⇒ Some(link(rel:="import",href:=imp,`type`:="text/html")) }
    val css = stylesheetLinks(args).map { sheet ⇒
      Some(link(rel:="stylesheet", href:=sheet, `type`:="text/css", media:="screen"))
    }
    val js = javascriptLinks(args).map { scr ⇒ Some(script(src:=scr,`type`:="text/javascript")) }
    val result = List[Option[HtmlElement]](
      alternate(args).map { alt ⇒ link(rel:="alternate", `type`:="text/html", href:=alt) },
      authorLink(args).map { auth ⇒ link(rel:="author", `type`:="text/html", href:=auth) },
      licenseLink(args).map { lic ⇒ link(rel:="license", `type`:="text/html", href:=lic) },
      iconLink(args).map { icon ⇒
        link(rel:="icon",href:=icon.url,`type`:=icon.typ, "sizes".attr:=s"${icon.x}x${icon.y}" )
      },
      favIcon(args).map { icon ⇒ link(rel:="shortcut icon", `type`:="image/x-icon", href:=icon) }
    ) ++ imps ++ css ++ js ++ otherLinks(args).map { l ⇒ Some(l.render) }
    result.flatten
  }

  def alternate(args: Arguments) : Option[String] = None
  def authorLink(args: Arguments) : Option[String] = None
  def licenseLink(args: Arguments) : Option[String] = None
  def favIcon(args: Arguments) : Option[String] = None
  def iconLink(args: Arguments) : Option[IconLinkTag] = None
  def imports(args: Arguments) : Seq[String] = Seq.empty[String]
  def javascriptLinks(args : Arguments) : Seq[String] = Seq.empty[String]
  def stylesheetLinks(args : Arguments) : Seq[String] = Seq.empty[String]
  def scripts(args : Arguments) : Seq[String] = Seq.empty[String]
  def sheets(args : Arguments) : Seq[String] = Seq.empty[String]
  def otherLinks(args : Arguments) : Seq[LinkTag] = Seq.empty[LinkTag]
}
