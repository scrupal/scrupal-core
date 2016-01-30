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
import play.api.UsefulException
import play.api.mvc.Results._

import play.api.mvc.{Results, Result, RequestHeader}
import play.api.routing.sird._
import scrupal.admin.AdminProvider
import scrupal.html.Help

import scala.concurrent.Future

case class SiteData(
  name : String,
  domainName: String = "localhost",
  description : String = "",
  requireHttps : Boolean = false,
  modified: Instant = Instant.now,
  created : Instant = Instant.now,
  oid : Option[Long] = None
) extends Slickery {
  def forHost(hostName: String) : Boolean = { hostName.endsWith(domainName) }
}

case class Site(data: SiteData)(implicit scrupal : Scrupal) extends {
    val registry: Registry[Site] = scrupal.sites
    val id: Symbol = Symbol(data.name)
} with EnablementProvider[Site] with Registrable[Site] {

  def appReactorFor(request : RequestHeader) = {
    provideFor[ApplicationProvider].lift(request)
  }

  def apiReactorFor(request : RequestHeader) = {
    provideFor[EntityProvider].lift(request)
  }

  override def reactorFor(request : RequestHeader) = {
    val delegates = mapIf[Provider]{ p : Enablee ⇒
      !(p.isInstanceOf[EntityProvider] || p.isInstanceOf[ApplicationProvider])
    } { e : Enablee ⇒
      e.asInstanceOf[Provider]
    }
    val routes = delegates.foldLeft(Provider.emptyReactionRoutes) {
      case (accum,next) ⇒
        accum.orElse(next.provide)
    }
    routes.lift(request)
  }

  def isChildScope(e : Enablement[_]) : Boolean = delegates.exists { x ⇒ x == e }

  def onDevServerError(request : RequestHeader, exception : UsefulException) : Future[Result] = {
    Future.successful ( InternalServerError(views.html.defaultpages.devError(None, exception)))
  }

  def onProdServerError(request : RequestHeader, exception : UsefulException) : Future[Result] = {
    Future.successful ( InternalServerError(views.html.defaultpages.error(exception)))
  }

  def onNotImplemented(request: RequestHeader, what: String) : Future[Result] = {
    Future.successful ( Results.NotImplemented(s"Not Implemented: $what"))
  }

  def onServiceUnavailable(request: RequestHeader, what: String) : Future[Result] = {
    Future.successful ( Results.ServiceUnavailable(s"ServiceUnavailable: $what"))
  }

  def onBadRequest(request : RequestHeader, message : String) : Future[Result] = {
    Future.successful ( Results.BadRequest(s"BadRequest: $request: $message") )
  }

  def onUnauthorized(request : RequestHeader, message: String) : Future[Result] = {
    Future.successful ( Results.Unauthorized(s"Unauthorized: $request: $message"))
  }

  def onForbidden(request : RequestHeader, message : String) : Future[Result] = {
    Future.successful ( Results.Forbidden(s"Forbidden: $request: $message") )
  }

  def onNotFound(request : RequestHeader, message : String) : Future[Result] = {
    Future.successful ( Results.NotFound(s"NotFound: $request: $message") )
  }

  def onGenericClientError(request: RequestHeader, status: Int, msg: String) : Future[Result] = {
    Future.successful ( Results.Status(status)(s"Error($status): $request $msg"))
  }

  def debugFooter : Boolean = true // TODO: Implement with Feature
}

class LocalHostSite(implicit scrupal : Scrupal) extends Site(SiteData("localhost"))(scrupal) {
  object helpProvider extends Provider with Enablee {
    def id = 'help
    def provide: ReactionRoutes = {
      case GET(p"/") ⇒
        new Reactor {
          def apply(stimulus: Stimulus) : Future[RxResponse] = {
            Help.page(stimulus).map { html ⇒
              Response(HtmlContent(html))
            }(scrupal.executionContext)
          }
        }
    }
  }
  object adminProvider extends AdminProvider
  enable(helpProvider)
  enable(adminProvider)
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
