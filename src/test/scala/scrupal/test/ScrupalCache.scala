package scrupal.test

import java.io.File

import com.google.inject.Guice
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Mode, Environment, Application}
import play.api.mvc.Handler

import com.reactific.helpers.AbstractRegistry
import scrupal.core.Scrupal
import scrupal.utils.MemoryCache

/** Scrupal Test Cache
  *
  * In Test Mode we can run tests in parallel and they may need their own instance of Scrupal. This cache
  * associates each test name with a Scrupal instance for the context of that test. Ensuring each test
  * case has its own isolated context means that conflicts between test cases will be eliminated. There
  * is no global data except the Scrupal object.
  */
object ScrupalCache extends MemoryCache[String,Scrupal] {

  def apply(
             name: String,
             path: java.io.File = new java.io.File("."),
             additionalConfiguration: Map[String, AnyRef] = Map.empty,
             withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty
           ): Scrupal = {
    getOrElse(name) {
      val environment: Environment = Environment(path, this.getClass.getClassLoader, Mode.Test)
      val dbName = name.replace(" ", "_")
      val config = additionalConfiguration ++ Map(
        "app.instance.name" -> s"$dbName",
        s"db.$dbName.driver" → "org.h2.Driver",
        s"db.$dbName.url" → s"jdbc:h2:db/./$dbName.db;FILE_LOCK=SOCKET;PAGE_SIZE=8192;AUTO_SERVER=TRUE",
        s"db.$dbName.username" → "sa",
        s"db.$dbName.password" → "Sof1ukS8B$ j9eeTech!"
      )
      val configuration: Configuration = Configuration.load(environment, config)

      val builder = new GuiceApplicationBuilder()
        .in(environment)
        .configure(configuration)

      val injector = Guice.createInjector(builder.applicationModule())

      val application = builder.build()

      injector.getInstance(classOf[Scrupal])
    }
  }
}
