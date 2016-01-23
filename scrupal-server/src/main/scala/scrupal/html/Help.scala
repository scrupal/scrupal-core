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

import scala.concurrent.Future
import scalatags.Text.all._

import scrupal.core.{Stimulus, HtmlContent}



object Help {

  def page( stimulus: Stimulus ) : Future[HtmlElement] = {
    val args = Map(
      "nav" → HtmlContent(span("nav")),
      "header" → HtmlContent(span("header")),
      "left" → HtmlContent(span("left")),
      "right" → HtmlContent(span("right")),
      "contents" → HtmlContent(index),
      "footer" → HtmlContent(span("footer")),
      "endscripts" → HtmlContent(emptyContents)
    )
    stimulus.context.scrupal.scrupalLayout(stimulus.context, args)
  }

  def index : HtmlElement = {
    h1("Welcome to Scrupal")
  }

}
