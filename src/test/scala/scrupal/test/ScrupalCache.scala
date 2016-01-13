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
      s"db.$dbName.driver" → "org.h2.Driver",
      s"db.$dbName.url" → s"jdbc:h2:db/./$dbName.db;FILE_LOCK=SOCKET;PAGE_SIZE=8192;AUTO_SERVER=TRUE",
      s"db.$dbName.username" → "sa",
      s"db.$dbName.password" → ""
    )
    ApplicationLoader.createContext(environment, config) → dbName
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
