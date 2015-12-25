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

import com.reactific.helpers.NotImplementedException

import java.io.InterruptedIOException
import java.sql.SQLTimeoutException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

import play.api._
import play.api.http.Status._
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc.{RequestHeader, Result, Results}
import play.api.routing.Router

import scala.concurrent.Future

class ScrupalErrorHandler @Inject()(scrupal: Scrupal, router : Router = Router.empty) extends
  DefaultHttpErrorHandler(scrupal.environment, scrupal.configuration, router=Some(router)) {

  private def forSiteAndSubdomain(request: RequestHeader)
    (found: (RequestHeader, Site, Option[String]) ⇒ Future[Result])
    (orElse: () ⇒ Future[Result]): Future[Result] = {
    scrupal.siteForRequest(request) match {
      case (Some(site), Some(subDomain)) ⇒
        found(request, site, Some(subDomain))
      case (Some(site), None) ⇒
        found(request, site, None)
      case _ ⇒
        orElse()
    }
  }

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onBadRequest(request, message, subDomain)
    } { () ⇒
      super.onBadRequest(request, message)
    }
  }

  override protected def onForbidden(request: RequestHeader, message: String): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onForbidden(request, message, subDomain)
    } { () ⇒
      super.onForbidden(request, message)
    }
  }

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onNotFound(request, message, subDomain)
    } { () ⇒
      super.onNotFound(request, message)
    }
  }

  protected def onUnauthorized(request: RequestHeader, message: String): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onUnauthorized(request, message, subDomain)
    } { () ⇒
      super.onOtherClientError(request, UNAUTHORIZED, message)
    }
  }

  protected def onGenericClientError(request: RequestHeader, status: Int, message: String): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onGenericClientError(request, status, message, subDomain)
    } { () ⇒
      Future.successful(
        Results.Status(status)(
          views.html.defaultpages.badRequest(request.method, request.uri, message)
        )
      )
    }
  }

  /**
    * Invoked when a client error occurs, that is, an error in the 4xx series, which is not handled by any of
    * the other methods in this class already.
    *
    * @param request    The request that caused the client error.
    * @param statusCode The error status code.  Must be greater or equal to 400, and less than 500.
    * @param message    The error message.
    */
  override protected def onOtherClientError(request: RequestHeader, status: Int, message: String): Future[Result] = {
    status match {
      case BAD_REQUEST ⇒
        onBadRequest(request, message)
      case UNAUTHORIZED ⇒
        onUnauthorized(request, message)
      case FORBIDDEN ⇒
        onForbidden(request, message)
      case NOT_FOUND ⇒
        onNotFound(request, message)
      case clientError if status >= 400 && status < 500 =>
        onGenericClientError(request, status, message)
      case nonClientError =>
        throw new IllegalArgumentException(
          s"onClientError invoked with non client error status code $status: $message"
        )
    }
  }

  def onNotImplemented(request: RequestHeader, exception: Throwable) : Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onNotImplemented(request, exception.getMessage, subDomain)
    } { () ⇒
      super.onServerError(request, exception)
    }
  }

  def onServiceUnavailable(request: RequestHeader, exception: Throwable) : Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onServiceUnavailable(request, exception.getMessage, subDomain)
    } { () ⇒
      super.onServerError(request, exception)
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable) : Future[Result] = {
    exception match {
      case x: NotImplementedError ⇒
        onNotImplemented(request, x)
      case x: NotImplementedException ⇒
        onNotImplemented(request, x)
      case x: TimeoutException ⇒
        onServiceUnavailable(request, x)
      case x: InterruptedException ⇒
        onServiceUnavailable(request, x)
      case x: SQLTimeoutException ⇒
        onServiceUnavailable(request, x)
      case x: InterruptedIOException ⇒
        onServiceUnavailable(request, x)
      case x: Throwable ⇒
        super.onServerError(request, exception)
    }
  }

  override protected def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    forSiteAndSubdomain(request) { (header, site, subDomain) ⇒
      site.onServerError(request, exception, subDomain)
    } { () ⇒
      super.onProdServerError(request, exception)
    }
  }


}

