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

package scrupal.router

import javax.inject.{Inject, Singleton}

import com.reactific.helpers.LoggingHelper
import play.api.http.HttpErrorHandler
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{RequestHeader, Action}
import scrupal.core.ScrupalBuildInfo

import scala.util.{Failure, Success, Try}


@Singleton
class Assets @Inject()(errHandler: HttpErrorHandler) extends controllers.Assets(errHandler) {

  val themes = Assets.getThemeInfo

  val versionMap : Map[String,String] = Map(
    "bootswatch" -> ScrupalBuildInfo.bootswatch_version,
    "font-awesome" -> ScrupalBuildInfo.font_awesome_version,
    "marked" -> ScrupalBuildInfo.marked_version
  )

  def mkPrefix(subdir: String, lib: String = "scrupal-core", version : String = ScrupalBuildInfo.version) = {
    s"/META-INF/resources/webjars/$lib/$version$subdir"
  }

  def at(file: String) = super.at(mkPrefix(""), file, aggressiveCaching=true)

  def js(file: String) = super.at(mkPrefix("/javascripts"), file)

  def img(file: String) = super.at(mkPrefix("/images"), file)

  def css(file: String) = super.at(mkPrefix("/stylesheets"), file)

  def bsjs(file: String)= {
    val path = s"/META-INF/resources/webjars/bootswatch/${ScrupalBuildInfo.bootswatch_version}/2/js"
    super.at(path, file)
  }

  def theme(theme: String) = {
    themes.get(theme) match {
      case Some(thm) ⇒
        val path = s"/META-INF/resources/webjars/bootswatch/${ScrupalBuildInfo.bootswatch_version}/${theme.toLowerCase}"
      case None ⇒
        Action { req: RequestHeader ⇒ NotFound(s"Theme '$theme'") }
    }
  }

  def webjar(library: String, file: String) = {
    versionMap.get(library) match {
      case Some(version) ⇒
        val path = s"/META-INF/resources/webjars/$library/$version"
        super.at(path, file)
      case None ⇒
        Action { req: RequestHeader ⇒ NotFound(s"WebJar '$library'") }
    }
  }

}

object Assets extends LoggingHelper {

  case class Theme(name: String, description: String, thumbnail: String, preview: String, css: String,
                   `css-min`: String)
  implicit val ThemeReads = Json.reads[Theme]
  case class ThemeInfo(version: String, themes: List[Theme])
  implicit val ThemeInfoReads = Json.reads[ThemeInfo]

  lazy val emptyThemeInfo = Map.empty[String,Theme]

  def getThemeInfo : Map[String,Theme] = Try[Map[String,Theme]] {
    val theme_path = s"META-INF/resources/webjars/bootswatch/${ScrupalBuildInfo.bootswatch_version}/2/api/themes.json"
    val loader = this.getClass.getClassLoader
    Option(loader.getResourceAsStream(theme_path)) match {
      case Some(stream) ⇒ {
        val jsval = Json.parse(stream)
        ThemeInfoReads.reads(jsval) match {
          case JsSuccess(value, path) ⇒ value.themes.map { t ⇒ t.name → t }.toMap
          case JsError(errors) ⇒
            log.warn(s"Failed to parse Bootswatch themes Json: ${JsError.toJson(errors)}")
            emptyThemeInfo
          case _ ⇒
            log.warn(s"Failed to parse Bootswatch themes Json: mismatched result")
            emptyThemeInfo
        }
      }
      case None ⇒
        log.warn(s"Failed to find Bootswatch themes resource at $theme_path")
        emptyThemeInfo
    }
  } match {
    case Success(x) ⇒ x
    case Failure(x) ⇒
      log.warn("Failed to acquire Bootswatch themes:", x)
      emptyThemeInfo
  }

}
