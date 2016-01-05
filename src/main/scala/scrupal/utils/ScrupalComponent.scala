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

import com.reactific.helpers.{ThrowableWithComponent, HelperComponent}

/** A Scrupal Component
  * This trait just provides logging and exception throwing support. Mix it in to a Scrupal class to obtain
  * these facilities. Other functionality may be added in the future.
  */
trait ScrupalComponent extends HelperComponent {
  override protected def mkThrowable(msg: String, cause : Option[Throwable] = None) : ThrowableWithComponent = {
    new ScrupalException(this, msg, cause)
  }
}


class ScrupalException(val component : ScrupalComponent, msg : String, cause: Option[Throwable] = None)
  extends Exception(msg,cause.orNull) with ThrowableWithComponent
