package scrupal.core

import java.time.Instant

import com.reactific.helpers.LoggingHelper
import com.typesafe.config.ConfigFactory

import play.api.Configuration

import scrupal.test.{SchemaFixture, ScrupalSpecification}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/** Test Case For CoreSchema */
class CoreSchemaSpec extends ScrupalSpecification("CoreSchema") {

  import com.reactific.helpers.LoggingHelper.ScalaLoggerExtension

  final val baseDir = "./target/testdb"

  log.setToDebug()
  LoggingHelper.setToDebug("com.reactific.slickery")
  log.debug("Starting CoreSchema test")

  def testDbConfig(name : String) : Configuration = {
    Configuration(ConfigFactory.parseString(
      s"""scrupal.database.$name {
         |  driver = "com.reactific.slickery.H2Driver$$"
         |  db {
         |    connectionPool = disabled
         |    driver = "org.h2.Driver"
         |    url = "jdbc:h2:$baseDir/$name"
         |  }
         |}""".stripMargin)
    )
  }

  log.debug(s"Config=${testDbConfig("core").toString}")

  case class CoreSchemaFixture() extends SchemaFixture[CoreSchema[_]]( CoreSchema(testDbConfig("core")))

  "CoreSchema" should {
    "CRUD SiteData" in CoreSchemaFixture() { schema : CoreSchema[_] ⇒
      val future = scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        log.debug("withExecutionContext")
        schema.create().flatMap { u ⇒
          log.debug("schema created")
          val site = SiteData("testSite", "foo.com", "testing only")
          schema.db.run(schema.sites.create(site)).flatMap { id : Long ⇒
            log.debug("site created")
            schema.db.run(schema.sites.byId(id)).flatMap { maybeSite : Option[SiteData] ⇒
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
      }
      await(future, 1.minute, "CRUD Sites").get
    }
  }
}
