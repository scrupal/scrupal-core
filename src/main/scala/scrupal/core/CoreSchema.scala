package scrupal.core

import com.reactific.slickery.{Schema ⇒ SlickerySchema}
import com.typesafe.config.Config
import slick.lifted.ProvenShape

/** The Core Database Schema.
  *
  * Description of thing
  */
case class CoreSchema(config : Option[Config] = None)(implicit scrupal : Scrupal)
  extends SlickerySchema("scrupal", "core",
    config.getOrElse(scrupal.configuration.underlying))(scrupal.executionContext) {

  import dbConfig.driver.api._

  val db = dbConfig.db

  class SiteRow(tag : Tag) extends UseableRow[SiteData](tag, "sites") {

    def domainName = column[String]("domainName")
    def requireHttps = column[Boolean]("requireHttps")

    override def * : ProvenShape[SiteData] = {
      (name, domainName, description, requireHttps, modified, created, oid.?) <> ((SiteData.apply _).tupled,
        SiteData.unapply )
    }
  }

  object sites extends UseableQuery[SiteData,SiteRow]( new SiteRow(_) )

  def schemas = sites.schema

}
