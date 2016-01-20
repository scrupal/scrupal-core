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

/* OLD Abstractions from pages.scala
TODO: Use or delete
trait BasicPageGenerator extends PageGenerator {
  def headSuffix(context : Context, args : ContentsArgs) : HtmlContents = {
    implicit val ctxt : Context = context
    Seq(
      link(rel := "stylesheet", href := PathOf.webjar("font-awesome", "css/font-awesome.min.css"), media := "screen"),
      link(rel := "stylesheet", href := PathOf.css("scrupal"), media := "screen"),
      webjar("marked", "marked.js")
    )
  }

  def bodyPrefix(context : Context, args : ContentsArgs) : HtmlContents = { display_alerts(context) }

  def bodySuffix(context : Context, args : ContentsArgs) : HtmlContents = {
    if (context.scrupal.Features.enabled('DebugFooter, context.scrupal)) {
      display_context_table(context)
    } else {
      Html.emptyContents
    }
  }
}

abstract class BasicPage(
  override val id : Symbol,
  override val title : String,
  override val description : String) extends TemplatePage(id, title, description) with BasicPageGenerator

trait BootstrapPageGenerator extends BasicPageGenerator {
  override def headSuffix(context : Context, args : ContentsArgs) : Html.Contents = {
    implicit val ctxt: Context = context
    Seq(
      link(rel := "stylesheet", media := "screen", href := PathOf.theme(ctxt.themeName)),
      webjar("jquery", "jquery.js"),
      script(`type` := "application/javascript", src := PathOf.bsjs("bootstrap.min.js")),
      script(`type` := "application/javascript", src := PathOf.bsjs("bootswatch.js"))
    ) ++ super.headSuffix(context)
  }

  def body_content(context : Context, args : ContentsArgs) : Contents = {
    Seq(span(em("OOPS!"), " You forgot to override body_content!"))
  }

  override def bodyMain(context : Context, args : ContentsArgs) : Contents = {
    Seq(div(cls := "container", body_content(context, args)))
  }
}

class BootstrapPage(
  override val id : Symbol,
  override val title : String,
  override val description : String) extends BasicPage(id, title, description) with BootstrapPageGenerator

trait MarkedPageGenerator extends BootstrapPageGenerator {
  override def headSuffix(context : Context, args : ContentsArgs) = {
    super.headSuffix(context, args) ++ Seq(
      webjar("marked", "marked.js")
    )
  }

  override def bodyMain(context : Context, args : ContentsArgs) : Contents = {
    Seq(div(scalatags.Text.all.id := "marked", body_content(context, args)))
  }

  override def bodySuffix(context : Context, args : ContentsArgs) : Contents = {
    Seq(js(
      """marked.setOptions({
        |  renderer: new marked.Renderer(),
        |  gfm: true,
        |  tables: true,
        |  breaks: true,
        |  pedantic: false,
        |  sanitize: false,
        |  smartLists: true,
        |  smartypants: false
        |});
        |var elem = document.getElementById('marked');
        |elem.innerHTML = marked(elem.innerHTML);""".stripMargin
    ))
  }
}

class MarkedPage(
  override val id : Symbol,
  override val title : String,
  override val description : String) extends BootstrapPage(id, title, description) with MarkedPageGenerator

trait ForbiddenPageGenerator extends BasicPageGenerator {
  def what : String
  def why : String
  override val title = "Forbidden - " + what
  override val description = "Forbidden Error Page"

  def bodyMain(context : Context, args : ContentsArgs) : Html.Contents = {
    danger(Seq(
      h1("Nuh Uh! I Can't Do That!"),
      p(em("Drat!"), s"Because $why, you can't $what. That's just the way it is."),
      p("You should try one of these options:"),
      ul(li("Type in another URL, or"), li("Try to get lucky with ",
        a(href := context.suggestURL.toString, "this suggestion")))
    ))()
  }
}

case class ForbiddenPage(
  override val id : Symbol,
  what : String,
  why : String) extends Html.Template(id) with ForbiddenPageGenerator

trait NotFoundPageGenerator extends BasicPageGenerator {
  def what : String
  def causes : Seq[String]
  def suggestions : Seq[String]
  def title : String = "Not Found - " + what
  def description : String = "Not Found Error Page"
  def bodyMain(context : Context, args : ContentsArgs) : Contents = {
    warning(Seq(
      h1("There's A Hole In THe Fabrice Of The InterWebz!"),
      p(em("Oops!"), "We couldn't find ", what, ". That might be because:"),
      ul({ for (c ← causes) yield { li(c) } },
        li("you used an old bookmark for which the resource is no longer available"),
        li("you mis-typed the web address.")
      ),
      p("You can try one of these options:"),
      ul({ for (s ← suggestions) yield { li(s) } },
        li("type in another URL, or "),
        li("Try to get lucky with ", a(href := context.suggestURL.toString, "this suggestion"))
      )
    ))()
  }
}

case class NotFoundPage(
  override val id : Symbol,
  what : String,
  causes : Seq[String] = Seq(),
  suggestions : Seq[String] = Seq()) extends Html.Template(id) with NotFoundPageGenerator

trait PlainPageGenerator extends BootstrapPageGenerator {
  def content(context : Context, args : ContentsArgs) : Html.Contents

  override def body_content(context: Context, args: ContentsArgs): Contents = {
    content(context, args)
  }
}

abstract class GenericPlainPage(_id : Symbol, title : String, description : String)
  extends BasicPage(_id, title, description) with PlainPageGenerator

case class PlainPage(
  override val id : Symbol,
  override val title : String,
  override val description : String,
  the_content : Html.Contents) extends GenericPlainPage(id, title, description) {
  def content(context: Context, args: ContentsArgs): Contents = the_content
}
                                       */

/* Old content from HTML.scala
TODO: Use or delete
object Html {
  type HtmlElement = TypedTag[String]
  type HtmlContents = Seq[Modifier]
  val emptyContents = Seq.empty[Modifier]

  def js(javascript : String) =
    script(`type` := "application/javascript", javascript)
  def jslib(lib : String, path : String) =
    script(`type` := "application/javascript", src := s"/assets/lib/$lib/$path")
  def webjar(lib: String, path: String) =
    script(`type` := "application/javascript", src := s"/webjar/$lib/$path")

  val nbsp = raw("&nbsp;")

  val foo = head()
  val bar = html()

  object ng {
    val app = "ng-app".attr
    val controller = "ng-controller".attr
    val show = "ng-show".attr
    val hide = "ng-hide".attr
  }

  def ng(name : String) = ("ng-" + name).attr

  def renderContents(contents : HtmlContents) : String = {
    val sb = new StringBuilder(4096)
    for (tag ← contents) { sb.append(tag.toString) }
    sb.toString()
  }

  type ContentsArgs = Map[String, Generator]
  val EmptyContentsArgs = Map.empty[String, Generator]

  trait Generator {
    def generate(context : Context, args : ContentsArgs) : HtmlContents
    def render(context : Context, args : ContentsArgs) : String
    def tag(tagName : String, context : Context, args : ContentsArgs) : HtmlContents = {
      args.get(tagName) match {
        case Some(v) ⇒ v.generate(context, args)
        case None    ⇒ Seq("")
      }
    }
  }

  trait SimpleGenerator extends Generator with (() ⇒ HtmlContents) {
    def generate(context : Context, args : ContentsArgs) : HtmlContents = {
      apply()
    }
    def render(context : Context, args : ContentsArgs) : String = {
      renderContents(apply())
    }
    override def toString() : String = renderContents(apply())
  }

  trait FragmentGenerator extends Generator with ((Context) ⇒ HtmlContents) {
    def generate(context : Context, args : ContentsArgs) : HtmlContents = {
      this.apply(context)
    }
    def render(context : Context, args : ContentsArgs) : String = {
      renderContents(apply(context))
    }
  }

  trait TemplateGenerator extends Generator with ((Context, ContentsArgs) ⇒ HtmlContents) {
    def generate(context : Context, args : ContentsArgs) : HtmlContents = {
      this.apply(context, args)
    }
    def render(context : Context, args : ContentsArgs) : String = {
      renderContents(apply(context, args))
    }
  }

  abstract class Template(_i : Symbol) extends {
    val id : Symbol = _i
  } with Registrable[Template] with Describable with TemplateGenerator {
    def registry = Template
  }

  object Template extends Registry[Template] {
    def registryName = "Html Templates"
    def registrantsName = "html template"
  }

  trait PageGenerator extends Describable with TemplateGenerator {
    def title : String
    def headTitle(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlElement = {
      tags2.title(title)
    }
    def headDescription(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlElement = {
      meta(name := "description", content := description)
    }
    def favIcon(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlElement = {
      link(rel := "shortcut icon", `type` := "image/x-icon", href := PathOf.favicon()(context))
    }
    def headSuffix(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlContents
    def headTag(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlElement = {
      head(
        headTitle(context, args),
        headDescription(context, args),
        meta(charset := "UTF-8"),
        meta(name := "viewport", content := "width=device-width, initial-scale=1.0"),
        favIcon(context, args),
        headSuffix(context, args)
      )
    }
    def bodyPrefix(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlContents
    def bodyMain(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlContents
    def bodySuffix(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlContents
    def bodyTag(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlElement = {
      body(
        bodyPrefix(context, args), bodyMain(context, args), bodySuffix(context, args)
      )
    }
    def apply(context : Context, args : ContentsArgs = EmptyContentsArgs) : HtmlContents = {
      Seq[HtmlElement](scalatags.Text.all.html(headTag(context, args), bodyTag(context, args)))
    }
    override def render(context : Context, args : ContentsArgs = EmptyContentsArgs) : String = {
      val sb = new StringBuilder(4096)
      sb.append("<!DOCTYPE html>")
      for (tag ← generate(context, args)) {
        sb.append(tag.toString)
      }
      sb.toString()
    }
  }

  abstract class Page(val title : String, val description : String) extends PageGenerator

  abstract class TemplatePage(_id : Symbol, val title : String, val description : String)
    extends Template(_id) with PageGenerator

  object Resp {
    def apply(tag : HtmlElement, disposition : Disposition) = Response(HtmlContent(tag.toString()), disposition)
    def apply(contents : Html.HtmlContents, disposition : Disposition) = {
      new HtmlResponse(Html.renderContents(contents), disposition)
    }
  }
}

*/
