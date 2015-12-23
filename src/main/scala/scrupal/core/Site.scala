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

import play.api.http.{HttpRequestHandler,HttpErrorHandler}
import play.api.mvc.{Result, Handler, RequestHeader}

import scala.concurrent.Future

case class Site(name : String) extends Registrable[Site] with HttpRequestHandler with HttpErrorHandler {
  def registry: Registry[Site] = Site
  def id: Symbol = Symbol(name)

  /** Get The Handler For The Request */
  def handlerForRequest(request: RequestHeader) : (RequestHeader, Handler) = {
    request -> null
  }

  def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = ???

  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = ???
}

object Site extends Registry[Site] {
  def registryName: String = "Sites"
  def registrantsName: String = "site"
}
