/***********************************************************************************************************************
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
  *********************************************************************************************************************/

package router.scrupal.core

import javax.inject.{Inject, Singleton}

import com.reactific.helpers.LoggingHelper
import controllers.Assets.Asset
import controllers.WebJarAssets
import org.webjars.WebJarAssetLocator
import play.api.http.HttpErrorHandler
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, RequestHeader}
import play.api.{Configuration, Environment, Mode}
import scrupal.core.ScrupalBuildInfo
import scrupal.utils.ScrupalComponent

import scala.util.{Failure, Success, Try}

@Singleton
class Assets @Inject() (
    errorHandler: HttpErrorHandler,
    configuration: Configuration,
    environment: Environment
) extends WebJarAssets(errorHandler, configuration, environment) with ScrupalComponent {

  import Assets.{webJarPrefix, assetPrefix}

  def root(file: String) = {
    val lookup = if (file.startsWith("/")) file else "/" + file
    super.at("/", lookup, aggressiveCaching=false)
  }

  def public(file: String) = super.at(assetPrefix(""), file, aggressiveCaching=false)

  def projectjs(file : String) = {
    super.at("/public",file)
  }

  def js(p : String, file: Asset) = {
    super.versioned(assetPrefix("javascripts", p), file)
  }
  def css(p : String, file: Asset) = {
    val prefix = assetPrefix("stylesheets", p)
    val path = file + ".min.css"
    super.versioned(prefix, path)
  }


  def img(file: String) = super.at(assetPrefix("images"), file, aggressiveCaching=false)

  def themeFromProvider(provider : String, name: String) : Action[AnyContent] = {
    provider match {
      case "bootswatch" ⇒ theme(name)
      case x ⇒ Action { NotFound(s"Theme $name in theme provider $provider") }
    }
  }
  def theme(theme: String) : Action[AnyContent] = {
    Assets.themes.get(theme) match {
      case Some(thm) ⇒
        if (environment.mode == Mode.Prod)
          Action { MovedPermanently(thm.cssCdn) }
        else {
          val themedir = if (theme.toLowerCase == "default" ) "flatly" else theme.toLowerCase
          val path = webJarPrefix("bootswatch", themedir)
          super.at(path, "bootstrap.min.css", aggressiveCaching = true)
        }
      case None ⇒
        Action { req: RequestHeader ⇒ NotFound(s"Theme '$theme'") }
    }
  }

  def webjar(webjar: String, file: String) : Action[AnyContent] = {
    val path = webJarPrefix(webjar,"")
    super.at(path, file, aggressiveCaching=false)
  }
}

object Assets extends LoggingHelper {

  lazy val themes = Assets.getThemeInfo

  lazy val versionMap : Map[String,String] = Map(
    "bootstrap"  -> ScrupalBuildInfo.bootstrap_version,
    "bootswatch" -> ScrupalBuildInfo.bootswatch_version,
    "font-awesome" -> ScrupalBuildInfo.font_awesome_version,
    "marked" -> ScrupalBuildInfo.marked_version,
    "jquery" → ScrupalBuildInfo.jquery_version,
    "modernizr" → ScrupalBuildInfo.modernizr_version,
    "scrupal-core" → ScrupalBuildInfo.version,
    "polymer" → ScrupalBuildInfo.polymer_version,
    "webcomponentsjs" → ScrupalBuildInfo.webcomponentsjs_version
  )

  final val webjar_prefix = s"/${WebJarAssetLocator.WEBJARS_PATH_PREFIX}"

  def webJarPrefix(webJar: String, partialPath: String) = {
    val version = versionMap.getOrElse(webJar,"{missing_version}")
    s"$webjar_prefix/$webJar/$version/$partialPath"
  }

  def assetPrefix(partialPath: String, prefix : String = "/public") = {
    s"$prefix/$partialPath"
  }

  /** Template
    * {{{
    * {
    *   "name": "Cosmo",
    *   "description": "An ode to Metro",
    *   "thumbnail": "https://bootswatch.com/cosmo/thumbnail.png",
    *   "preview": "https://bootswatch.com/cosmo/",
    *   "css": "https://bootswatch.com/cosmo/bootstrap.css",
    *   "cssMin": "https://bootswatch.com/cosmo/bootstrap.min.css",
    *   "cssCdn": "https://maxcdn.bootstrapcdn.com/bootswatch/latest/cosmo/bootstrap.min.css",
    *   "less": "https://bootswatch.com/cosmo/bootswatch.less",
    *   "lessVariables": "https://bootswatch.com/cosmo/variables.less",
    *   "scss": "https://bootswatch.com/cosmo/_bootswatch.scss",
    *   "scssVariables": "https://bootswatch.com/cosmo/_variables.scss"
    * }
    * }}}
    */
  case class Theme(name: String, description: String, thumbnail: String, preview: String, css: String,
      cssMin: String, cssCdn: String, less: String, lessVariables: String, scss: String, scssVariables : String)

  implicit val ThemeReads = Json.reads[Theme]
  case class ThemeInfo(version: String, themes: List[Theme])
  implicit val ThemeInfoReads = Json.reads[ThemeInfo]

  lazy val emptyThemeInfo = Map.empty[String,Theme]

  def getThemeInfo : Map[String,Theme] = Try[Map[String,Theme]] {
    val theme_path = webJarPrefix("bootswatch","api") + "/3.json"
    val loader = this.getClass.getClassLoader
    Option(loader.getResourceAsStream(theme_path.drop(1))) match {
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
