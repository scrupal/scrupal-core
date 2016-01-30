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
package scrupal.core

import java.io.File

import controllers.Assets.Asset
import org.specs2.matcher.MatchResult
import play.api.Mode.Mode
import play.api.http.{DefaultHttpErrorHandler, Status}
import play.api.mvc.{Action, AnyContent, Request}
import play.api.test.{FakeRequest, WithApplication}
import play.api.{Configuration, Environment, Mode}
import scrupal.test.{SharedTestScrupal, ScrupalSpecification}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class AssetsSpec extends ScrupalSpecification("Assets") with SharedTestScrupal {

  def mkAssets(mode : Mode = Mode.Test) : Assets = {
    val environment = Environment(new File("."), this.getClass.getClassLoader, mode)
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

    def checkAssetStatus(action: Action[AnyContent], status : Int = Status.OK) : MatchResult[Int] = {
      val future = action(request).map { result ⇒
        result.header.status must beEqualTo(status)
      }
      Await.result(future, 2.seconds)
    }

    "locate logback.xml three ways" in new WithApplication(scrupal.application) {
      val assets = mkAssets()
      checkAssetStatus(assets.at("/", "/logback.xml"))
      checkAssetStatus(assets.root("logback.xml"))
      checkAssetStatus(assets.root("/logback.xml"))
    }

    "locate scrupal.ico with img" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().img("scrupal.ico"))
    }
    "locate scrupal.ico with public" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().public("images/scrupal.ico"))
    }

    "locate scrupal.css with css" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().css(Asset("scrupal.min.css")))
    }

    "locate scrupal.js with js" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().js(Asset("scrupal.js")))
    }

    "locate Simplex theme with theme(Simplex)" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().theme("Simplex"))
    }

    "locate Simplex with explicit theme provider" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().themeFromProvider("bootswatch", "Simplex"))
    }

    "not find Simplex with unknown theme provider" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().themeFromProvider("unknown", "Simplex"), Status.NOT_FOUND)
    }

    "not find Duplex theme with theme(Duplex)" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().theme("Duplex"),Status.NOT_FOUND)
    }

    "yield MovedPermanently For Production Theme" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets(Mode.Prod).theme("Simplex"), Status.MOVED_PERMANENTLY)
    }

    "locate Simplex with webjar" in new WithApplication(scrupal.application) {
      checkAssetStatus(mkAssets().webjar("bootswatch", "simplex/bootstrap.min.css"))
    }
    "locate scrupal-jsapp js files" in new WithApplication(scrupal.application) {
      val assets = mkAssets()
      checkAssetStatus(assets.projectjs("scrupal-jsapp-fastopt.js"))
      checkAssetStatus(assets.projectjs("scrupal-jsapp-jsdeps.js"))
      checkAssetStatus(assets.projectjs("scrupal-jsapp-fastopt.js.map"))
      checkAssetStatus(assets.projectjs("scrupal-jsapp-launcher.js"))
    }
  }
}
