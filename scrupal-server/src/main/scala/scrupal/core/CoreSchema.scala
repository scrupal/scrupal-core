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

import akka.http.scaladsl.model.{MediaTypes, MediaType}
import com.reactific.slickery._
import com.typesafe.config.Config
import play.api.Configuration
import scrupal.utils.ScrupalComponent

import scala.reflect.ClassTag

/** The Core Database Schema.
  *
  * Description of thing
  */
abstract class CoreSchema[DRVR <: SlickeryDriver](
    configPath : String,
    config : Config
)(implicit scrupal : Scrupal, ct : ClassTag[DRVR])
  extends Schema[DRVR]("ScrupalCore", configPath, config)(scrupal.executionContext, ct) {

  import driver.api._

  class SiteRow(tag : Tag) extends SlickeryRow[SiteData](tag, "sites") {
    def domainName = column[String](nm("domainName"))
    def requireHttps = column[Boolean](nm("requireHttps"))
    override def * = (name, domainName, description, requireHttps, modified, created, oid.?) <>
      ((SiteData.apply _).tupled, SiteData.unapply )
  }

  object sites extends SlickeryQuery[SiteData,SiteRow]( new SiteRow(_) ) {
    def mapNames() : DBIOAction[Map[Long,String],NoStream,Effect.Read] = {
      this.map(s ⇒ (s.oid,s.name)).result.map(_.toMap)(scrupal.executionContext)
    }
  }

  implicit lazy val mediaTypeMapper = MappedColumnType.base[MediaType,String] (
    { mt => s"${mt.mainType}/${mt.subType}" },
    { s =>
      val parts = s.toLowerCase().split('/')
      require(parts.length == 2, "Invalid media type in database")
      MediaTypes.getForKey(parts(0) → parts(1)).getOrElse(MediaTypes.`application/octet-stream`) }
  )

  class NodeRow(tag : Tag) extends SlickeryRow[StoredNode](tag, "nodes") {
    def payload = column[Array[Byte]](nm("payload"))
    def mediaType = column[MediaType](nm("mediatype"))
    override def * = (name, description, payload, mediaType, modified, created, oid.?) <>
      ((StoredNode.apply _).tupled, StoredNode.unapply )
  }

  object nodes extends SlickeryQuery[StoredNode,NodeRow]( new NodeRow(_) )

  def schemas = Map("sites" → sites.schema, "nodes" → nodes.schema)

}

case class CoreSchema_PG(configPath: String, config : Config)(implicit scrupal : Scrupal)
  extends CoreSchema[PostgresDriver](configPath, config)(scrupal, ClassTag(classOf[PostgresDriver]))

case class CoreSchema_H2(configPath : String, config : Config)(implicit scrupal : Scrupal)
  extends CoreSchema[H2Driver](configPath, config)(scrupal, ClassTag(classOf[H2Driver]))

object CoreSchema extends ScrupalComponent {
  def apply(configPath : String, config : Config)(implicit scrupal : Scrupal) : CoreSchema[_] = {
    SupportedDB.forConfig(configPath, config) match {
      case Some(db) ⇒
        db match {
          case PostgresQL ⇒
            CoreSchema_PG(configPath, config)
          case H2 ⇒
            CoreSchema_H2(configPath, config)
          case _ ⇒
            toss(s"Unsupported database type: ${db.kindName} while opening Scrupal Core DB")
        }
      case None ⇒
        toss(s"Configuration path '$configPath' not found")
    }
  }
  def apply(configPath : String, configuration : Configuration)(implicit scrupal: Scrupal) : CoreSchema[_] = {
    configuration.getConfig("scrupal.database") match {
      case Some(config) ⇒
        apply(configPath, config.underlying)
      case None ⇒
        toss(s"Configuration path 'scrupal.database' not found")
    }
  }
}

