package scrupal.test

import play.api.inject.DefaultApplicationLifecycle
import play.api.inject.guice.GuiceApplicationBuilder
import play.api._
import play.api.mvc.Handler

import com.reactific.helpers.MemoryCache
import scrupal.core.Scrupal

/** Scrupal Test Cache
  *
  * In Test Mode we can run tests in parallel and they may need their own instance of Scrupal. This cache
  * associates each test name with a Scrupal instance for the context of that test. Ensuring each test
  * case has its own isolated context means that conflicts between test cases will be eliminated. There
  * is no global data except the Scrupal object.
  */
object ScrupalCache extends MemoryCache[String,Scrupal] {

  def apply(name: String,
            path: java.io.File = new java.io.File("."),
             additionalConfiguration: Map[String, AnyRef] = Map.empty,
             withRoutes: PartialFunction[(String, String), Handler] = PartialFunction.empty
           ) : Scrupal = {
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

      val builder = new GuiceApplicationBuilder(environment, configuration, Seq.empty, Seq.empty, Seq.empty)
      val injector = builder.injector()
      val application : Application = builder.build()
      val applicationLifecycle = injector.instanceOf(classOf[DefaultApplicationLifecycle])
      val actors = if (application.mode == Mode.Test) Some(application.actorSystem) else None

      new Scrupal(name, environment, configuration, applicationLifecycle, injector, application, actors)
    }
  }
}
