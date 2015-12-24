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

import java.time.Instant

import com.reactific.helpers.{Registrable, Registry}
import com.reactific.slickery.{Modifiable, Describable, Nameable, Storable}

import play.api.mvc.{Handler, RequestHeader}

case class Site(name : String,
  domainName: String = "localhost",
  description : String = "",
  modified: Option[Instant] = Some(Instant.now()),
  created : Option[Instant] = Some(Instant.now()),
  oid : Option[Long] = None
)(implicit val scrupal : Scrupal) extends {
    val registry: Registry[Site] = scrupal.sites
    val id: Symbol = Symbol(name)
} with EnablementProvider[Site] with Registrable[Site]
  with Storable with Nameable with Describable with Modifiable {

  def requireHttps : Boolean = false

  def forHost(hostName: String) : Boolean = { hostName.endsWith(domainName) }

  def reactorFor(request: RequestHeader, subdomain: String) : Option[Reactor] = {
    reactorFor(request)
  }

  /** Get The Handler For The Request */
  def handlerForRequest(request: RequestHeader) : (RequestHeader, Handler) = {
    request -> null
  }

  def isChildScope(e : Enablement[_]) : Boolean = delegates.exists { x ⇒ x == e }
}

case class SitesRegistry() extends Registry[Site] {
  def registryName: String = "Sites"
  def registrantsName: String = "site"

  import scala.language.reflectiveCalls

  def forHost(hostName : String) : Seq[Site] = { select { case (id,site) => site.forHost(hostName) }.toSeq }

}
