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

import com.reactific.helpers.{Registrable, Registry}

import play.api.mvc.{Handler, RequestHeader}

import scala.util.matching.Regex

case class Site(name : String, hostNames: Regex = ".*".r)(implicit val scrupal : Scrupal) extends Registrable[Site]
  with ScrupalUser {
  def registry: Registry[Site] = scrupal.sites
  def id: Symbol = Symbol(name)

  def requireHttps : Boolean = false

  def forHost(hostName: String) : Boolean = {
    hostNames.findFirstIn(hostName).isDefined
  }

  /** Get The Handler For The Request */
  def handlerForRequest(request: RequestHeader) : (RequestHeader, Handler) = {
    request -> null
  }
}

case class SitesRegistry() extends Registry[Site] {
  def registryName: String = "Sites"
  def registrantsName: String = "site"

  import scala.language.reflectiveCalls

  def forHost(hostName : String) : Iterable[Site] = {
    for (
      (id, site) ← _registry if site.forHost(hostName)
    ) yield {
      site
    }
  }

}
