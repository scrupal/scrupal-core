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
package scrupal

import play.api.http.Writeable
import scrupal.core.Context

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/** Scrupal html package
  * This package provides a variety of utilities for writing HTML, CSS and JavaScript documents. It is based on Li
  * Haoyi's scalatags.
  */
package object html {

  type HtmlElement = scalatags.Text.TypedTag[String]
  type HtmlFragment = scalatags.Text.Frag
  type HtmlContents = Seq[scalatags.Text.Frag]
  val emptyContents : HtmlContents = Seq.empty[scalatags.Text.Frag]

  implicit class HtmlContentsExtensions(contents : HtmlContents) {
    def render : String = {
      val strb = new StringBuilder()
      for (frag ← contents) { strb.append(frag.render) }
      strb.toString()
    }
  }

  implicit def writeableForHtmlContents(implicit ec : ExecutionContext) : Writeable[HtmlContents] =
    Writeable[HtmlContents]({ hc: HtmlContents ⇒ hc.render.getBytes },  Some("text/html"))(ec)

  implicit def writeableForHtmlElement(implicit ec : ExecutionContext) : Writeable[HtmlElement] =
    Writeable[HtmlElement]({ he : HtmlElement ⇒ he.render.getBytes }, Some("text/html"))(ec)

  implicit def elemToContents(e : HtmlElement) : HtmlContents = Seq(e)

  /** Simple functor that generates Html Content.
    * All objects that can generate HTMl Content from a Context should inherit this function type
    */
  trait HtmlContentsGenerator extends ( (Context) ⇒ HtmlContents )

  trait HtmlElementGenerator extends ( (Context) ⇒ HtmlContents )

  trait SimpleGenerator extends HtmlElementGenerator with (() ⇒ HtmlContents) {
    def apply(context : Context) : HtmlContents = { apply() }
  }

  /** Arranger Function.
    *
    * An arranger is a function that does the essential layout arrangement for a Layout. It takes in a Context and a
    * tag mapping and produces an `Array[Byte]` result. The Layout trait extends this function so that its apply method
    * can be used to perform the arranging.
    *
    */
  type Arrangement = Map[String,HtmlContentsGenerator]

  trait Arranger extends ((Context, Arrangement) ⇒ Future[HtmlElement])

  /** Assets Accessor
    * This is just a utility for easier access to the Assets class
    */
  val assets = router.scrupal.core.routes.Assets

  import scalatags.Text.all._

  lazy val `http-equiv` = "http-equiv".attr

  def js(javascript : String) =
    script(`type` := "application/javascript", javascript)
  def jslib(lib : String, path : String) =
    script(`type` := "application/javascript", src := s"/assets/lib/$lib/$path")
  def webjar(lib: String, path: String) =
    script(`type` := "application/javascript", src := s"/webjar/$lib/$path")

  val nbsp = raw("&nbsp;")

}
