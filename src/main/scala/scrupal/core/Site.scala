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

import com.reactific.helpers.{MemoryCache, Registrable, Registry}
import com.reactific.slickery._
import play.api.http.Status

import play.api.mvc.{Results, Result, Handler, RequestHeader}

import scala.concurrent.Future

case class SiteData(
  name : String,
  domainName: String = "localhost",
  description : String = "",
  requireHttps : Boolean = false,
  modified: Instant = Instant.EPOCH,
  created : Instant = Instant.EPOCH,
  oid : Option[Long] = None
) extends Useable {
  def forHost(hostName: String) : Boolean = { hostName.endsWith(domainName) }
}

case class Site(data: SiteData)(implicit val scrupal : Scrupal) extends {
    val registry: Registry[Site] = scrupal.sites
    val id: Symbol = Symbol(data.name)
} with EnablementProvider[Site] with Registrable[Site] {

  def reactorFor(request: RequestHeader, subdomain: String) : Option[Reactor] = {
    reactorFor(request)
  }

  /** Get The Handler For The Request */
  def handlerForRequest(request: RequestHeader) : (RequestHeader, Handler) = {
    request -> null
  }

  def isChildScope(e : Enablement[_]) : Boolean = delegates.exists { x ⇒ x == e }

  def onServerError(request : RequestHeader, exception : Throwable, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.InternalServerError(s"Requested failed: $request: $exception ($subDomain)") )
  }

  def onNotImplemented(request: RequestHeader, what: String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.NotImplemented(s"Not Implemented: $what ($subDomain)"))
  }

  def onServiceUnavailable(request: RequestHeader, what: String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.ServiceUnavailable(s"ServiceUnavailable: $what ($subDomain)"))
  }

  def onBadRequest(request : RequestHeader, message : String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.BadRequest(s"BadRequest: $request: $message ($subDomain)") )
  }

  def onUnauthorized(request : RequestHeader, message: String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.Unauthorized(s"Unauthorized: $request: $message ($subDomain)"))
  }

  def onForbidden(request : RequestHeader, message : String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.Forbidden(s"Forbidden: $request: $message ($subDomain)") )
  }

  def onNotFound(request : RequestHeader, message : String, subDomain: Option[String]) : Future[Result] = {
    Future.successful ( Results.NotFound(s"NotFound: $request: $message ($subDomain)") )
  }

  def onGenericClientError(request: RequestHeader, status: Int, msg: String, sub: Option[String]) : Future[Result] = {
    Future.successful ( Results.Status(status)(s"Error($status): $request $msg ($sub)"))
  }
}

case class SitesRegistry() extends Registry[Site] {
  def registryName: String = "Sites"
  def registrantsName: String = "site"

  private val byDomainName = new MemoryCache[String,Site]

  override final def register(site : Site) : Unit = {
    super.register(site)
    byDomainName.getOrElse(site.data.domainName)(site)
  }

  override final def unregister(site: Site) : Unit = {
    super.unregister(site)
    byDomainName.remove(site.data.domainName)
  }

  def forHost(hostName : String) : Option[Site] = {
    byDomainName.get(hostName)
  }

}
