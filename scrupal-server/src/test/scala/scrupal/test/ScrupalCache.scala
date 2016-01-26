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

import play.api._

import com.reactific.helpers.MemoryCache
import scrupal.core.Scrupal

/** Scrupal Test Cache
  *
  * In Test Mode we can run tests in parallel and they need their own instance of Scrupal. This cache
  * associates each test name with a Scrupal instance for the context of that test. Ensuring each test
  * case has its own isolated context means that conflicts between test cases will be eliminated. There
  * is no global data except the Scrupal object.
  */
object ScrupalCache extends MemoryCache[String,Scrupal] {

  def makeContext(
      name : String,
      path: java.io.File = new java.io.File("."),
      additionalConfiguration: Map[String, AnyRef] = Map.empty
  ) : (ApplicationLoader.Context, String) = {
    val environment: Environment = Environment(path, this.getClass.getClassLoader, Mode.Test)
    val dbName = Scrupal.mkActorName(name)
    val config = additionalConfiguration ++ Map(
      "play.akka.actor-system" → dbName,
      "app.instance.name" -> dbName,
      s"scrupal.database.$dbName.driver" → "com.reactific.slickery.H2Driver$",
      s"scrupal.database.$dbName.db.driver" → "org.h2.Driver",
      s"scrupal.database.$dbName.db.url" → s"jdbc:h2:./db/$dbName;FILE_LOCK=SOCKET;PAGE_SIZE=8192;AUTO_SERVER=TRUE",
      s"scrupal.database.$dbName.db.username" → "sa",
      s"scrupal.database.$dbName.db.password" → ""
    )
    ApplicationLoader.createContext(environment, config) → dbName
  }

  def unload(name : String) : Unit = {
    super.remove(name)
  }

  def apply(
      name: String,
      path: java.io.File = new java.io.File("."),
      additionalConfiguration: Map[String, AnyRef] = Map.empty
  ) : Scrupal = {
    getOrElse(name) {
      val (context, newName) = makeContext(name, path, additionalConfiguration)
      new Scrupal(context, newName)
    }
  }
}
