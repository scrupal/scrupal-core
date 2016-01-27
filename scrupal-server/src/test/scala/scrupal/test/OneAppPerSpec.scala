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

package scrupal.test

import javax.inject.{Inject, Provider}

import org.specs2.data.AlwaysTag
import org.specs2.specification.core
import org.specs2.specification.core.Fragments
import play.api._
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{Handler, RequestHeader}
import play.api.routing.Router

import scala.runtime.AbstractPartialFunction

trait OneAppPerSpec extends ScrupalSpecification with SharedTestScrupal {

  /** Override app if you need a FakeApplication with other than default parameters. */
  implicit lazy val application : Application = scrupal.application

  private def startRun() = {
    Play.start(application)
  }

  private def stopRun() = {
    Play.stop(application)
  }

  override def map(fs: =>core.Fragments) = {
    super.map(fs).
      prepend(fragmentFactory.step(startRun())).
      append(fragmentFactory.step(stopRun()))
  }
}

private case class FakeRouterConfig(withRoutes: PartialFunction[(String, String), Handler])

private class FakeRoutes(
  injected: PartialFunction[(String, String), Handler], fallback: Router) extends Router {
  def documentation = fallback.documentation
  // Use withRoutes first, then delegate to the parentRoutes if no route is defined
  val routes = new AbstractPartialFunction[RequestHeader, Handler] {
    override def applyOrElse[A <: RequestHeader, B >: Handler](rh: A, default: A => B) =
      injected.applyOrElse((rh.method, rh.path), (_: (String, String)) => default(rh))
    def isDefinedAt(rh: RequestHeader) = injected.isDefinedAt((rh.method, rh.path))
  } orElse new AbstractPartialFunction[RequestHeader, Handler] {
    override def applyOrElse[A <: RequestHeader, B >: Handler](rh: A, default: A => B) =
      fallback.routes.applyOrElse(rh, default)
    def isDefinedAt(x: RequestHeader) = fallback.routes.isDefinedAt(x)
  }
  def withPrefix(prefix: String) = {
    new FakeRoutes(injected, fallback.withPrefix(prefix))
  }
}

private class FakeRouterProvider @Inject() (config: FakeRouterConfig, parent: RoutesProvider) extends Provider[Router] {
  lazy val get: Router = new FakeRoutes(config.withRoutes, parent.get)
}

