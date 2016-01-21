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

import scrupal.test.{HTML5Validator, ScrupalSpecification}

abstract class ValidatingSpecification(n : String) extends ScrupalSpecification(n) {

  def doValidation(name: String, doc : String, context: String = "") : Seq[Throwable] = {
    val errors = if (context.isEmpty)
      HTML5Validator.validate(doc)
    else
      HTML5Validator.validateFragment(doc, context)
    if (errors.nonEmpty)
      log.debug(s"Validation errors for $name: \n$doc\n${errors.mkString("\n")}")
    errors
  }

  def validate(name : String, doc : String, context : String = "") = {
    doValidation(name, doc, context).isEmpty must beTrue
  }
  def invalidate(name : String, doc : String, context : String = "") = {
    doValidation(name, doc, context).nonEmpty must beTrue
  }
}


class HTML5ValidatorSpec extends ValidatingSpecification("Validator") {

  "HTML5Validtator" should {
    "accept valid html fragment" in {
      validate("valid html", "<div class=\"centered\"><p>Some text</p></div>", "body")
    }
    "reject invalid html fragment" in {
      invalidate("invalid html", "<div<pfoo", "body")
    }
  }
}
