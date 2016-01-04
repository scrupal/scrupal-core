package scrupal.core

import java.net.URL

import play.twirl.api.{Txt, JavaScript, Html}

/** Simple functor that generates Html Content.
  * All objects that can generate HTMl Content from a Context should inherit this function type
  */
trait HtmlGenerator extends ((Context) ⇒ Html)

/** Generate Content For The {{{<head>}}} Element Of An Html Page.
  * Converts Scala objects into the corresponding HTML via the use of Twirl
  * @param title The text of the title element
  * @param base The HTML base element
  * @param meta The HTML meta element
  * @param links The HTML link elements
  * @param style Additional style text
  * @param javascript Additional JavaScript program text
  */
case class PageHeadTags(
    title: String,
    base: Option[BaseTag] = None,
    meta: MetaTags = MetaTags(),
    links: LinkTags = LinkTags(),
    style: Option[Txt] = None,
    javascript: Option[JavaScript] = None
) extends HtmlGenerator {
  def appy(context: Context) : Html = {
    scrupal.core.html.head(context, this)
  }
}

/** HTML Base Tag
  * Represents the HTML {{{<base href="@htref" target="@target">}}} tag and converts to HTML content
  * @param href The default url to use for links on the page
  * @param target The default link target ("_blank" or "_self" typically)
  */
case class BaseTag(href: String, target: String)

/** HTML Meta Tags
  * Represents the HTML {{{<meta>}}} elements in the head of a page and generates HTML
  * @param description A description for the page
  * @param author The author of the page
  * @param generator The software that generated the page
  * @param appName The name of the application the page is a part of
  * @param keywords A sequence of words to include in the keywords meta element
  */
case class MetaTags(
    description: Option[String] = None,
    author: Option[String] = None,
    generator : Option[String] = None,
    appName: Option[String] = None,
    keywords: Seq[String] = Seq.empty[String],
    refresh: Int = 0
) extends HtmlGenerator {
  def apply(context : Context) : Html = {
    scrupal.core.html.meta(context, this)
  }
}

/** Representation For An Icon Link tag */
case class IconLinkTag(url: URL, x: Int, y: Int, typ: String)

/** HTML Link Tags
  * Represents the HTML {{{<link}}} elements in the head of a page and generates HTML
  * @param alternate An link for an alternate form of this page
  * @param author A link for more information on the author
  * @param license A link to the license or copyright for this page
  * @param icon The document icon to show for this page
  * @param scriptLinks Links to JavaScript source files or jars
  * @param stylesheets Links to CSS style sheets
  */
case class LinkTags(
    alternate: Option[URL] = None,
    author: Option[URL] = None,
    license: Option[URL] = None,
    icon : Option[IconLinkTag] = None,
    scriptLinks: Seq[String] = Seq.empty[String],
    stylesheets: Seq[String] = Seq.empty[String]
) extends HtmlGenerator {
  def apply(context: Context) : Html = {
    scrupal.core.html.link(context, this)
  }
}
