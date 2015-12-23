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
package scrupal.router

import java.io.File

import play.api.{Configuration, Mode, Environment}
import play.api.http.DefaultHttpErrorHandler
import scrupal.test.ScrupalSpecification

class AssetsSpec extends ScrupalSpecification("Assets") {

  def mkAssets : Assets = {
    val environment = Environment(new File("."), this.getClass.getClassLoader, Mode.Test)
    val configuration = Configuration.load(environment)
    new Assets(new DefaultHttpErrorHandler(environment, configuration))
  }

  val expectedThemes = Seq("Simplex", "Cosmo", "Spacelab", "Superhero", "Flatly", "Amelia", "Readable",
    "Slate", "Cyborg", "United", "Journal", "Spruce", "Cerulean")

  "Assets" should {
    "generate theme names" in {
      val themes = mkAssets.themes.keySet
      for (name <- expectedThemes) {
        themes.contains(name) must beTrue
      }
      success("All themes found")
    }
  }
}
