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

package scrupal.utils

import java.util.concurrent.TimeoutException

import com.reactific.helpers.{TossedException, ThrowingHelper, LoggingHelper}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ Await, Future }
import scala.util.{ Failure, Success, Try }

/** A Scrupal Component
  * This trait just provides logging and exception throwing support. Mix it in to a Scrupal class to obtain
  * these facilities.
  */
trait ScrupalComponent extends LoggingHelper with ThrowingHelper {

  /** Handle Exceptions From Asynchronous Results
    * This awaits a future operation that returns HTML and converts any exception that arises during the
    * future operation or waiting into HTML as well. So, this is guaranteed to never let an exception escape and to
    * always return Html.
 *
    * @param future The Future[Html] to wait for
    * @param duration How long to wait, at most
    * @param opName The name of the operation being waited for, to assist with error messages
    * @return A blob of Html containing either the intended result or an error message.
    */
  def await[X](future : ⇒ Future[X], duration : FiniteDuration, opName : String) : Try[X] = {
    Try {
      Await.result(future, duration)
    } match {
      case Success(result) =>
        Success(result)
      case Failure(xcptn) =>
        xcptn match {
          case ix: InterruptedException =>
            // - if the current thread is interrupted while waiting
            Failure(TossedException(this, s"Operation '$opName' was interrupted: ", ix))
          case tx: TimeoutException =>
            // if after waiting for the specified time awaitable is still not ready
            Failure(TossedException(this, s"Operation '$opName' timed out after $duration", tx))
          case iax: IllegalArgumentException =>
            // if atMost is Duration.Undefined
            Failure(TossedException(this, s"Operation '$opName' called with untenable 'duration' argument", iax))
          case xcptn: Throwable =>
            Failure(TossedException(this, s"Operation '$opName'interrupted by unrecognized exception:", xcptn))
        }
    }
  }
}
