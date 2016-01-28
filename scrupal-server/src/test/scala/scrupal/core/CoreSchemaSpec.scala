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

import com.reactific.helpers.LoggingHelper
import com.typesafe.config.ConfigFactory

import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import scrupal.test.ScrupalSpecification

/** Test Case For CoreSchema */
class CoreSchemaSpec extends ScrupalSpecification("CoreSchema")  {

  import com.reactific.helpers.LoggingHelper.ScalaLoggerExtension

  "CoreSchema" should {
    log.setToDebug()
    LoggingHelper.setToDebug("com.reactific.slickery.*")
    log.debug("Starting CoreSchema test")

    "CRUD SiteData" in withScrupalSchema("site_crud") { (scrupal, schema) ⇒
      log.debug(s"JDBC Source: ${schema.db.source}")
      val future = scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        val site = SiteData("testSite", "foo.com", "testing only")
        val creation = schema.sites.create(site)
        schema.db.run(creation).flatMap { id : Long ⇒
          log.debug("site created")
          val retrieval = schema.sites.byId(id)
          schema.db.run(retrieval).flatMap { maybeSite : Option[SiteData] ⇒
            maybeSite.isDefined must beTrue
            val site = maybeSite.get
            log.debug(s"site retrieved: $site")
            site.name must beEqualTo("testSite")
            site.domainName must beEqualTo( "foo.com")
            site.description must beEqualTo("testing only")
            site.isCreated must beTrue
            site.isModified must beTrue
            site.oid.isDefined must beTrue
            site.oid must beEqualTo(Some(id))
            site.modified must beLessThan(Instant.now())
            site.created must beLessThan(Instant.now())
            val updated = site.copy(modified = Instant.now())
            schema.db.run(schema.sites.update(updated)).flatMap { count : Int ⇒
              log.debug(s"site updated: $count")
              count must beEqualTo(1)
              schema.db.run(schema.sites.delete(id)).map { count: Int ⇒
                log.debug(s"site deleted: $count")
                count must beEqualTo(1)
              }
            }
          }
        }
      }
      await(future, 1.minute, "CRUD Sites").get
    }

    "map oid/site names" in withScrupalSchema("map_site_names") { (scrupal, schema) ⇒
      val future = scrupal.withExecutionContext { implicit ec : ExecutionContext ⇒
        val site = SiteData("testSite", "foo.com", "testing only")
        schema.db.run {
          schema.sites.create(site).flatMap { oid ⇒ schema.sites.mapNames() }
        }
      }
      val map = await(future)
      map.values must contain("testSite")
    }

    "CRUD Node" in withScrupalSchema("node_crud") { (scrupal,schema) ⇒
      val future = scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        val when = Instant.now()
        StoredNode.fromContent("foo", "fooness", TextContent("bar"), when, when).flatMap { node : StoredNode ⇒
          schema.db.run(schema.nodes.create(node)).flatMap { id : Long ⇒
            schema.db.run(schema.nodes.byId(id)).flatMap { maybeNode : Option[StoredNode] ⇒
              maybeNode.isDefined must beTrue
              val otherNode = maybeNode.get
              otherNode.name must beEqualTo("foo")
              otherNode.description must beEqualTo("fooness")
              otherNode.created must beEqualTo(when)
              otherNode.modified must beEqualTo(when)
              otherNode.mediaType must beEqualTo(node.mediaType)
              java.util.Arrays.equals(otherNode.payload, node.payload) must beTrue
              val updated = otherNode.copy(modified = Instant.now())
              schema.db.run(schema.nodes.update(updated)).flatMap { count : Int ⇒
                log.debug(s"node updated: $count")
                count must beEqualTo(1)
                schema.db.run(schema.nodes.delete(id)).map { count : Int ⇒
                  log.debug(s"node deleted: $count")
                  count must beEqualTo(1)
                }
              }
            }
          }
        }
      }
      await(future, 1.minute, "CRUD Sites").get
    }

    "construct from configuration" in withScrupal("from_config") { (scrupal) ⇒
      val config = H2Config("fromConfig")
      val configObj = config.getObject("fromConfig")
      val hashmap = new java.util.HashMap[String,AnyRef]
      hashmap.put("scrupal.database.fromConfig", configObj)
      val config2 = ConfigFactory.parseMap( hashmap )
      val conf = Configuration( config2 )
      val cs = CoreSchema("fromConfig", conf)(scrupal)
      cs.schemaName must beEqualTo("ScrupalCore")
    }
  }
}
