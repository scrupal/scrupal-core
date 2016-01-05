package scrupal.core

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
    supportedDB: SupportedDB[DRVR],
    config : Config
)(implicit scrupal : Scrupal, ct : ClassTag[DRVR])
  extends Schema[DRVR]("scrupal", supportedDB, "core", config)(scrupal.executionContext, ct) {

  import driver.api._

  class SiteRow(tag : Tag) extends SlickeryRow[SiteData](tag, "sites") {

    def domainName = column[String]("domainName")
    def requireHttps = column[Boolean]("requireHttps")

    override def * = (name, domainName, description, requireHttps, modified, created, oid.?) <>
      ((SiteData.apply _).tupled, SiteData.unapply )
  }

  object sites extends SlickeryQuery[SiteData,SiteRow]( new SiteRow(_) )

  def schemas = Map("sites" → sites.schema)

}

case class CoreSchema_PostgresQL(config : Config)(implicit scrupal : Scrupal)
  extends CoreSchema[PostgresDriver](PostgresQL, config)(scrupal, ClassTag(classOf[PostgresDriver]))

case class CoreSchema_H2(config : Config)(implicit scrupal : Scrupal)
  extends CoreSchema[H2Driver](H2, config)(scrupal, ClassTag(classOf[H2Driver]))

object CoreSchema extends ScrupalComponent {
  def apply(config : Config)(implicit scrupal : Scrupal) : CoreSchema[_] = {
    SupportedDB.forConfig(config, "core") match {
      case Some(db) ⇒
        db match {
          case PostgresQL ⇒
            CoreSchema_PostgresQL(config)
          case H2 ⇒
            CoreSchema_H2(config)
          case _ ⇒
            toss(s"Unsupported database type: ${db.kindName} while opening Scrupal Core DB")
        }
      case None ⇒
        toss(s"Configuration path 'core' not found")
    }
  }
  def apply(configuration : Configuration)(implicit scrupal: Scrupal) : CoreSchema[_] = {
    configuration.getConfig("scrupal.database") match {
      case Some(config) ⇒
        apply(config.underlying)
      case None ⇒
        toss(s"Configuration path 'scrupal.database' not found")
    }
  }
}

