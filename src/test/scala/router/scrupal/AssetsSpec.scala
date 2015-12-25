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
package router.scrupal

import java.io.File

import org.specs2.matcher.MatchResult
import play.api.http.{DefaultHttpErrorHandler, Status}
import play.api.mvc.{Action, AnyContent, Request}
import play.api.test.{FakeRequest, WithApplication}
import play.api.{Configuration, Environment, Mode}
import scrupal.core.ScrupalBuildInfo
import scrupal.test.ScrupalSpecification

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class AssetsSpec extends ScrupalSpecification("Assets") {

  def mkAssets : Assets = {
    val environment = Environment(new File("."), this.getClass.getClassLoader, Mode.Test)
    val configuration = Configuration.load(environment)
    new Assets(new DefaultHttpErrorHandler(environment, configuration), configuration, environment)
  }

  val expectedThemes = Seq("Cerulean", "Cosmo", "Cyborg", "Darkly", "Flatly", "Journal", "Lumen",
    "Paper", "Readable", "Sandstone", "Simplex", "Slate", "Spacelab", "Superhero", "United", "Yeti")

  val request : Request[AnyContent] = FakeRequest("GET", "/thing").withHeaders("Host" → "localhost:80")

  "Assets" should {
    "have empty themeInfo" in {
      Assets.emptyThemeInfo.isEmpty must beTrue
    }

    "generate theme names" in {
      val themes = Assets.themes.keySet
      for (name <- expectedThemes) {
        themes.contains(name) must beTrue
      }
      success("All themes found")
    }

    def foundAsset(action: Action[AnyContent]) : MatchResult[Int] = {
      val future = action(request).map { result ⇒
        result.header.status must beEqualTo(Status.OK)
      }
      Await.result(future, 2.seconds)
    }

    "locate logger.xml with at" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.at("/", "/logger.xml"))
    }

    "locate logger.xml with root" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.root("logger.xml"))
    }

    "locate scrupal.ico with img" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.img("scrupal.ico"))
    }

    "locate scrupal.css with css" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.css("scrupal"))
    }

    "locate scrupal.js with js" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.js("scrupal.js"))
    }

    "locate Simplex theme with theme(Simplex)" in new WithApplication(scrupal.application) {
      foundAsset(mkAssets.theme("Simplex"))
    }

/*  52   def bsjs(file: String)= {
      53     val path = s"/META-INF/resources/webjars/bootswatch/${ScrupalBuildInfo.bootswatch_version}/2/js"
      54     super.at(path, file)
      55   }
    56
    57   def theme(theme: String) = {
      58     themes.get(theme) match {
      59       case Some(thm) ⇒
          60         val path = s"/META-INF/resources/webjars/bootswatch/${ScrupalBuildInfo.bootswatch_version}/${theme.toLowerCase}"
          61       case None ⇒
          62         Action { req: RequestHeader ⇒ NotFound(s"Theme '$theme'") }
          63     }
      64   }
    65
    66   def webjar(library: String, file: String) = {
      67     versionMap.get(library) match {
      68       case Some(version) ⇒
          69         val path = s"/META-INF/resources/webjars/$library/$version"
          70         super.at(path, file)
          71       case None ⇒
          72         Action { req: RequestHeader ⇒ NotFound(s"WebJar '$library'") }
          73     }
      74   }
    75
    76 }
             */
  }
}
