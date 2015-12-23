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

import javax.inject.Inject

import play.api.http.{HttpErrorHandlerExceptions, DefaultHttpErrorHandler}
import play.api.mvc.{Results, Result, RequestHeader}
import play.api.mvc.Results._
import play.api.{Logger, Mode, UsefulException}

import scala.concurrent.Future
import scala.util.control.NonFatal

class ErrorHandler @Inject()(scrupal: Scrupal) extends
  DefaultHttpErrorHandler(scrupal.environment, scrupal.configuration, router=Some(scrupal.globalRouter)) {

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  /**
    * Invoked when a client makes a request that was forbidden.
    *
    * @param request The forbidden request.
    * @param message The error message.
    */
  override def onForbidden(request: RequestHeader, message: String) = {
    Future.successful(
      Forbidden("You're not allowed to access this resource.")
    )
  }
  /**
    * Invoked when a client makes a bad request.
    *
    * @param request The request that was bad.
    * @param message The error message.
    */
  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] =
    Future.successful(BadRequest(views.html.defaultpages.badRequest(request.method, request.uri, message)))

  protected def onForbidden(request: RequestHeader, message: String): Future[Result] =
    Future.successful(Forbidden(views.html.defaultpages.unauthorized()))

  /**
    * Invoked when a handler or resource is not found.
    *
    * @param request The request that no handler was found to handle.
    * @param message A message.
    */
  protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(NotFound(environment.mode match {
      case Mode.Prod => views.html.defaultpages.notFound(request.method, request.uri)
      case _ => views.html.defaultpages.devNotFound(request.method, request.uri, router)
    }))
  }

  /**
    * Invoked when a client error occurs, that is, an error in the 4xx series, which is not handled by any of
    * the other methods in this class already.
    *
    * @param request The request that caused the client error.
    * @param statusCode The error status code.  Must be greater or equal to 400, and less than 500.
    * @param message The error message.
    */
  protected def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    Future.successful(Results.Status(statusCode)(views.html.defaultpages.badRequest(request.method, request.uri, message)))
  }

  /**
    * Invoked when a server error occurs.
    *
    * By default, the implementation of this method delegates to [[onProdServerError()]] when in prod mode, and
    * [[onDevServerError()]] in dev mode.  It is recommended, if you want Play's debug info on the error page in dev
    * mode, that you override [[onProdServerError()]] instead of this method.
    *
    * @param request The request that triggered the server error.
    * @param exception The server error.
    */
  def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    try {
      val usefulException = HttpErrorHandlerExceptions.throwableToUsefulException(sourceMapper,
        environment.mode == Mode.Prod, exception)

      logServerError(request, usefulException)

      environment.mode match {
        case Mode.Prod => onProdServerError(request, usefulException)
        case _ => onDevServerError(request, usefulException)
      }
    } catch {
      case NonFatal(e) =>
        Logger.error("Error while handling error", e)
        Future.successful(InternalServerError)
    }
  }

  /**
    * Responsible for logging server errors.
    *
    * This can be overridden to add additional logging information, eg. the id of the authenticated user.
    *
    * @param request The request that triggered the server error.
    * @param usefulException The server error.
    */
  protected def logServerError(request: RequestHeader, usefulException: UsefulException) {
    Logger.error("""
                   |
                   |! @%s - Internal server error, for (%s) [%s] ->
                   | """.stripMargin.format(usefulException.id, request.method, request.uri),
      usefulException
    )
  }

  /**
    * Invoked in dev mode when a server error occurs.
    *
    * @param request The request that triggered the error.
    * @param exception The exception.
    */
  protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] =
    Future.successful(InternalServerError(views.html.defaultpages.devError(playEditor, exception)))

  /**
    * Invoked in prod mode when a server error occurs.
    *
    * Override this rather than [[onServerError()]] if you don't want to change Play's debug output when logging errors
    * in dev mode.
    *
    * @param request The request that triggered the error.
    * @param exception The exception.
    */
  protected def onProdServerError(request: RequestHeader, exception: UsefulException): Future[Result] =
    Future.successful(InternalServerError(views.html.defaultpages.error(exception)))


}
