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

package scrupal.test

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.StandardCharsets

import nu.validator.validation.SimpleDocumentValidator
import nu.validator.xml.SystemErrErrorHandler
import org.xml.sax.{SAXParseException, ErrorHandler, InputSource}

import scala.util.{Failure, Success, Try}
import scala.collection.mutable

import scrupal.utils.ScrupalComponent


class CollectingErrorHandler extends ErrorHandler {
  val warnings = mutable.MutableList[SAXParseException]()
  val errors = mutable.MutableList[SAXParseException]()
  val fatals = mutable.MutableList[SAXParseException]()

  def warning(e: SAXParseException ) = {
    warnings += e
  }

  def error(e: SAXParseException) = {
    errors += e
  }

  def fatalError(e : SAXParseException) {
    fatals += e
  }

  def errorCount : Int = errors.size + fatals.size
  def warningCount : Int = warnings.size

  def isClean : Boolean = errors.isEmpty && fatals.isEmpty && warnings.isEmpty

  def collectAll : List[SAXParseException] = fatals.toList ++ errors.toList ++ warnings.toList
}

object HTML5Validator extends ScrupalComponent {

  def withValidator[T](document: String, context: String = "")(f : (InputSource,CollectingErrorHandler) ⇒ T) : Try[T] = {
    Try {
      val doc = context match {
        case "html" ⇒
          s"<!DOCTYPE html><html>$document</html>"
        case "head" ⇒
          s"<!DOCTYPE html><html><head><title>foo</title>$document</head><body></body></html>"
        case "body" ⇒
          s"<!DOCTYPE html><html><head><title>foo</title></head><body>$document</body></html>"
        case "div" ⇒
          s"<!DOCTYPE html><html><head><title>foo</title></head><body><div>$document</div></body></html>"
        case "" ⇒
          document
        case s: String ⇒
          s"<!DOCTYPE html><html><head><title>foo</title></head><body><$s>$document</$s></body></html>"
      }
      val stream : InputStream = new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8))
      try {
        val is = new InputSource(stream)
        val validator = new SimpleDocumentValidator(true)
        val errorHandler = new CollectingErrorHandler
        validator.setUpMainSchema( "http://s.validator.nu/html5-all.rnc", new SystemErrErrorHandler())
        validator.setUpValidatorAndParsers(errorHandler, true, false)
        validator.checkHtmlInputSource( is )
        f(is,errorHandler)
      } finally {
        stream.close()
      }
    }
  }

  def validate(document : String) : List[SAXParseException] = {
    withValidator(document) { (is, errorHandler) ⇒
      errorHandler.collectAll
    } match {
      case Success(x) ⇒
        x
      case Failure(x) ⇒
        log.warn("HTML5 validation failed:", x)
        List.empty[SAXParseException]
    }
  }

  def validateFragment(document : String, contextElement : String = "div") : List[SAXParseException] = {
    withValidator(document, contextElement) { (is, errorHandler) ⇒
      errorHandler.collectAll
    } match {
      case Success(x) ⇒
        x
      case Failure(x) ⇒
        log.warn("HTML5 validation failed:", x)
        List.empty[SAXParseException]
    }
  }
}
