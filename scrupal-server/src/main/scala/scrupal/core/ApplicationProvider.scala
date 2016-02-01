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

package scrupal.core

import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.routing.sird._

import scala.concurrent.Future

/** Base Class For Application Server Side
  * The ApplicationProvider defines the standard routes that all applications must provide. Applications should
  * derive from this class and define their own routes for custom server-side behavior.
  */
trait ApplicationProvider extends SingularProvider with Enablee {

  def scrupal : Scrupal

  def singularRoutes: ReactionRoutes = {
    case rh @ GET(p"/config") ⇒
      config(rh)
    case rh @ GET(p"/info") ⇒
      info(rh)
    case rh @ GET(p"/") ⇒
      loadApplication(rh)
    case rh @ GET(p"") ⇒
      loadApplication(rh)
  }

  def config(request : RequestHeader) : Reactor = {
    Reactor { stimulus: Stimulus ⇒
      implicit val ec = stimulus.context.scrupal.executionContext
      Future {
        val jsValue = Json.obj(
          "name" → label,
          "layout" → "ThreeColumns",
          "menu" → "static"
        )
        Response(JsonContent(jsValue))
      }
    }
  }
  def loadApplication(request : RequestHeader) : Reactor = {
    Reactor { stimulus: Stimulus ⇒
      implicit val ec = stimulus.context.scrupal.executionContext
      scrupal.reactPolymerLayout.page(stimulus.context, Map("appName" → label)).map { content : String ⇒
        Response(HtmlContent.raw(content), Successful)
      }
    }
  }

  final def info(request : RequestHeader) : Reactor = {
    Reactor { stimulus: Stimulus ⇒
      implicit val ec = stimulus.context.scrupal.executionContext
      Future {
        val jsValue = Json.obj(
          "name" → label
        )
        Response(JsonContent(jsValue))
      }
    }
  }

}
