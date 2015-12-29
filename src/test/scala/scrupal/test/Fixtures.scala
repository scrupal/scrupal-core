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

import com.reactific.slickery.Schema
import org.specs2.execute.{Result, AsResult}
import org.specs2.specification.Fixture

/** Testing Fixture for an arbitrary class */
class ClassFixture[CLASS](create : ⇒ CLASS) extends Fixture[CLASS] {
  def apply[R : AsResult](f : CLASS ⇒ R) = {
    AsResult( f (create) )
  }
}

/** Testing fixture for a class that needs to be closed when the test is over */
class CloseableFixture[CLASS <: AutoCloseable](create : ⇒ CLASS) extends Fixture[CLASS] {
  def apply[R](f : CLASS ⇒ R)(implicit evidence : AsResult[R]) : Result = {
    val fixture = create
    try {
      AsResult(f(fixture))
    } finally {
      fixture.close()
    }
  }
}

/** Testing fixture support for a case class (which should inherit this fixture class */
class CaseClassFixture[T <: CaseClassFixture[T]] extends Fixture[T] {
  def apply[R : AsResult](f : T ⇒ R) = {
    AsResult(f (this.asInstanceOf[T]))
  }
}

class SchemaFixture[T <: Schema](create: () ⇒ T) extends Fixture[T] {
  def apply[R](f : T ⇒ R)(implicit evidence : AsResult[R]) : Result = {
    val fixture = create()
    try {
      AsResult(f(fixture))
    } finally {
      fixture.close()
    }
  }
}
