package scrupal.core

import java.time.Instant

import com.reactific.helpers.LoggingHelper
import com.reactific.slickery.{PostgresQL, H2}
import com.reactific.slickery.testkit.SlickerySpecification

import com.typesafe.config.{ConfigFactory, Config}
import org.specs2.execute.{Result, AsResult}
import play.api.Configuration

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import scrupal.test.ScrupalSpecification

/** Test Case For CoreSchema */
class CoreSchemaSpec extends ScrupalSpecification("CoreSchema") with SlickerySpecification {

  import com.reactific.helpers.LoggingHelper.ScalaLoggerExtension

  final val baseDir = "target/testdb"

  def testH2DbConfig(name : String) : Config = H2.makeDbConfigFor(name, dir=baseDir, disableConnectionPool=true)

  def testPGDbConfig(name : String) : Config = PostgresQL.makeDbConfigFor(name, dir=baseDir, disableConnectionPool=true)

  def WithCoreSchema[R](dbName: String)(f : (CoreSchema[_]) ⇒ R)(implicit ev: AsResult[R]) : Result = {
    val h2Name = s"${dbName}_h2"
    val h2Config = testH2DbConfig(h2Name)
    log.debug(s"$h2Name: ${h2Config.toString}")
    WithH2Schema[CoreSchema_H2,R](h2Name)( CoreSchema_H2(_, h2Config))(f)

    val pgName = s"${dbName}_pg"
    val pgConfig = testPGDbConfig(pgName)
    WithPostgresSchema[CoreSchema_PG,R](pgName)(CoreSchema_PG(_, pgConfig))(f)
  }

  "CoreSchema" should {
    log.setToDebug()
    LoggingHelper.setToDebug("com.reactific.slickery.*")
    LoggingHelper.setToDebug("org.h2.*")
    LoggingHelper.setToDebug("slick.*")
    log.debug("Starting CoreSchema test")

    "CRUD SiteData" in {
      WithCoreSchema("site_crud") { schema : CoreSchema[_] ⇒
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
              site.isCreated must beFalse
              site.isModified must beFalse
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
    }

    "CRUD Node" in {
      WithCoreSchema("node_crud") { schema : CoreSchema[_] ⇒
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
    }
    "construct from configuration" in {
      val config = testH2DbConfig("fromConfig")
      val configObj = config.getObject("fromConfig")
      val hashmap = new java.util.HashMap[String,AnyRef]
      hashmap.put("scrupal.database.fromConfig", configObj)
      val config2 = ConfigFactory.parseMap( hashmap )
      val conf = Configuration( config2 )
      val cs = CoreSchema("fromConfig", conf)
      cs.schemaName must beEqualTo("ScrupalCore")
    }
  }
}
