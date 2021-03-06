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

import akka.actor.ActorSystem
import akka.util.Timeout

import com.reactific.helpers._
import com.reactific.slickery.Storable.OIDType

import java.lang.Thread.UncaughtExceptionHandler
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicInteger

import play.api._
import play.api.http._
import play.api.i18n.I18nComponents
import play.api.inject.{NewInstanceInjector, SimpleInjector, Injector, DefaultApplicationLifecycle}
import play.api.libs.Files.{DefaultTemporaryFileCreator, TemporaryFileCreator}
import play.api.libs.{Crypto, CryptoConfigParser, CryptoConfig}
import play.api.libs.concurrent.ActorSystemProvider
import play.api.mvc.{Request, AnyContent, EssentialFilter, RequestHeader}
import play.api.routing.Router
import play.api.routing.sird._
import scrupal.admin.AdminProvider

import scrupal.html._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import scrupal.utils.{DomainNames, ScrupalComponent}

class ScrupalLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) : Application = {
    new Scrupal(context).application
  }
}

case class Scrupal (
  context : ApplicationLoader.Context,
  name : String = "Scrupal"
) extends {
  final val id: Symbol = Symbol(name)
  final val registry = Scrupal
} with ScrupalComponent with AutoCloseable  with Registrable[Scrupal] with I18nComponents with WithCoreSchema {

  val applicationLifecycle: DefaultApplicationLifecycle = new DefaultApplicationLifecycle

  val httpFilters: Seq[EssentialFilter] = Seq.empty[EssentialFilter]

  val httpRequestHandler : ScrupalRequestHandler = new ScrupalRequestHandler(this)

  val httpErrorHandler: ScrupalErrorHandler = new ScrupalErrorHandler(this)

  val tempFileCreator: TemporaryFileCreator = new DefaultTemporaryFileCreator(applicationLifecycle)

  val configuration = context.initialConfiguration

  val environment = context.environment

  val sourceMapper = context.sourceMapper

  val webCommands = context.webCommands

  val actorSystem: ActorSystem = new ActorSystemProvider(environment, configuration, applicationLifecycle).get

  implicit val executionContext: ExecutionContext = getExecutionContext

  val httpConfiguration: HttpConfiguration = HttpConfiguration.fromConfiguration(configuration)

  val assets = new Assets(httpErrorHandler, configuration, environment)

  val scrupalController  = ScrupalController(this, messagesApi)

  val cryptoConfig: CryptoConfig = new CryptoConfigParser(environment, configuration).get

  val crypto: Crypto = new Crypto(cryptoConfig)

  val router : Router =
    new _root_.router.Routes(httpErrorHandler, scrupalController, assets, "/")

  val injector: Injector =
    new SimpleInjector(NewInstanceInjector) + router + crypto + httpConfiguration + tempFileCreator

  val application: Application = new DefaultApplication(environment, applicationLifecycle, injector,
    configuration, httpRequestHandler, httpErrorHandler, actorSystem, Plugins.empty)

  val author = "Reactific Software LLC"
  val copyright = "© 2013-2015 Reactific Software LLC. All Rights Reserved."
  val license = OSSLicense.ApacheV2

  implicit val akkaTimeout = getTimeout

  val sites : SitesRegistry = SitesRegistry()

  val layouts : LayoutRegistry = LayoutRegistry()

  implicit val scrupal : Scrupal = this

  val defaultPageLayout = new DefaultPageLayout
  val simpleBootstrapLayout = new SimpleBootstrapLayout
  val threeColumnBootstrapLayout = new ThreeColumnBootstrapLayout
  val reactPolymerLayout = new ReactPolymerLayout

  val localHostSite = new LocalHostSite()(this)

  applicationLifecycle.addStopHook { () ⇒
    Future.successful {
      this.internalClose()
    }
  }

  def close() : Unit = {
    doClose()
  }

  def closeTimeout : FiniteDuration = 10.seconds

  def doClose() : Boolean = {
    val futureResult = applicationLifecycle.stop()
    await(futureResult, closeTimeout, "scrupal shutdown") match {
      case Success(x) =>
        true
      case Failure(x) =>
        log.warn("Scrupal shutdown failed: ", x)
        false
    }
  }

  protected def internalClose() : Unit = {
    log.info("Scrupal shutdown initiated")
    // _storeContext.close()
    val executionContextIsAkka = executionContext == actorSystem.dispatcher
    actorSystem.shutdown()
    if (!executionContextIsAkka) {
      executionContext match {
        case es: ExecutorService ⇒
          es.shutdown()
        case _ ⇒
          log.warn("Execution context is not an ExecutorService, shutdown not possible")
      }
    }
    log.info("Scrupal shutdown completed normally.")
  }

  def stimulusForRequest(request : Request[AnyContent]) : Stimulus = {
    Stimulus(contextForRequest(request),request)
  }

  def contextForRequest(request : RequestHeader) : Context = {
    siteForRequest(request) match {
      case Some(site) ⇒
        Context(this, site)
      case _ ⇒
        Context(this)
    }
  }

  def siteForRequest(header: RequestHeader) : Option[Site] = {
    DomainNames.matchDomainName(header.domain) match {
      case (Some(domain),Some(subDomain)) =>
        sites.forHost(subDomain + "." + domain)
      case (Some(domain), None) ⇒
        sites.forHost(domain)
      case _ =>
        None
    }
  }

  // implicit val _assetsLocator: AssetsLocator = getAssetsLocator

  //   implicit protected val _storeContext : StoreContext

  def withConfiguration[T](f: (Configuration) ⇒ T): T = {
    f(configuration)
  }

  def withExecutionContext[T](f: (ExecutionContext) ⇒ T): T = {
    f(executionContext)
  }

  def withActorSystem[T](f: (ActorSystem) ⇒ T): T = {
    f(actorSystem)
  }

  def withActorExec[T](f: (ActorSystem, ExecutionContext, Timeout) ⇒ T): T = {
    f(actorSystem, executionContext, akkaTimeout)
  }

  def withApplication[T](f : (Application) ⇒ T) : T = { f(application) }

  lazy val schema : CoreSchema[_] = {
    CoreSchema.apply(name, configuration)(this)
  }

  protected def getTimeout: Timeout = {
    Timeout(
      configuration.getMilliseconds("scrupal.timeout.response").getOrElse(8000L), TimeUnit.MILLISECONDS
    )
  }

  /** Scrupal Thread Factory
    * This thread factory just names and numbers the threads created so we have a monotonically increasing number of
    * threads in the pool. It also ensures these are not Daemon threads and that there is an UncaughtExceptionHandler
    * in place that will log the escaped exception but otherwise take no action. These things help with with
    * identification of the threads during debugging and knowing that we have an escaped exception.
    */
  private object ScrupalThreadFactory extends ThreadFactory {
    val counter = new AtomicInteger(0)
    val ueh = new UncaughtExceptionHandler {
      def uncaughtException(t: Thread, x: Throwable) = {
        log.error("Exception escaped thread: " + t.getName + ", Id: " + t.getId + " error:", x)
      }
    }

    def newThread(r: Runnable) = {
      val result = new Thread(r)
      result.setDaemon(false)
      result.setUncaughtExceptionHandler(ueh)
      val num = counter.incrementAndGet()
      result.setName(s"$name-$num")
      result
    }
  }

  private object ScrupalRejectionHandler extends RejectedExecutionHandler {
    def rejectedExecution(r: Runnable, executor: ThreadPoolExecutor): Unit = {
      log.error(s"Execution rejected for $r in executor $executor")
    }
  }


  protected def getExecutionContext: ExecutionContext = {

    def makeFixedThreadPool(config: Configuration): ExecutionContext = {
      val numThreads = config.getInt("num-threads").getOrElse(16)
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(numThreads))
    }

    def makeWorkStealingPool(): ExecutionContext = {
      ExecutionContext.fromExecutorService(Executors.newWorkStealingPool())
    }

    def makeThreadPoolExecutionContext(config: Configuration): ExecutionContext = {
      val corePoolSize = config.getInt("core-pool-size").getOrElse(16)
      val maxPoolSize = config.getInt("max-pool-size").getOrElse(corePoolSize * 2)
      val keepAliveTime = config.getInt("keep-alive-secs").getOrElse(60)
      val queueCapacity = config.getInt("queue-capacity").getOrElse(maxPoolSize * 8)
      val queue = new ArrayBlockingQueue[Runnable](queueCapacity)

      ExecutionContext.fromExecutorService(
        new ThreadPoolExecutor(
          corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue, ScrupalThreadFactory,
          ScrupalRejectionHandler)
      )
    }

    configuration.getConfig("scrupal.executor") match {
      case Some(conf) ⇒
        conf.getString("type") match {
          case Some("default") ⇒
            makeWorkStealingPool()
          case Some("akka") ⇒
            actorSystem.dispatcher
          case Some("fixed-thread-pool") ⇒
            makeFixedThreadPool(conf.getConfig("fixed-thread-pool").getOrElse(Configuration()))
          case Some("work-stealing-pool") ⇒
            makeWorkStealingPool()
          case Some("thread-pool") ⇒
            makeThreadPoolExecutionContext(conf.getConfig("thread-pool").getOrElse(Configuration()))
          case Some(s: String) ⇒
            toss("Invalid value for configuration key 'scrupal.executor.type'")
          case None ⇒
            makeWorkStealingPool()
        }
      case None ⇒
        makeWorkStealingPool()
    }
  }
}

object Scrupal extends Registry[Scrupal] {
  def registryName = "Scrupalz"
  def registrantsName = "scrupali"

  def mkActorName(name : String) : String = {
    if (name.isEmpty) "NoName" else name.replaceAll("[^-0-9A-Za-z_.~!@]","-")
  }
}


  /*
    // NOTE: Because implicits are used during construction, the order of initialization here is important.

    implicit val _assetsLocator: AssetsLocator = getAssetsLocator


    protected def getAssetsLocator: AssetsLocator = {
      new ConfiguredAssetsLocator(_configuration)
    }

    protected def getStoreContext: StoreContext = {
      val configToSearch: Configuration = {
        _configuration.getConfig("scrupal") match {
          case None ⇒
            toss("Invalid configuration provided to scrupal. No 'scrupal' at top level")
          case Some(config1) ⇒
            config1.getString("storage.config.file") match {
              case Some(configFileName) ⇒
                ConfigHelpers.from(configFileName) match {
                  case Some(config2) ⇒
                    config2
                  case None ⇒
                    log.warn(s"The configured file name, '$configFileName' did not contain storage configuration. Trying default.")

                    getDefaultStoreConfig(_configuration)
                }
              case None ⇒
                log.warn("The key 'storage.config.file' is missing from scrupal configuration. Trying default.")
                getDefaultStoreConfig(_configuration)
            }
        }
      }
      Await.result(Storage.fromConfiguration(Some(configToSearch), "scrupal", create = true), 2.seconds)
    }




    def getDefaultStoreConfig(config: Configuration): Configuration = {
      config.getConfig("scrupal.storage.default").getOrElse {
        toss("Scrupal configuration is missing default storage configuration in scrupal.storage.default")
      }
    }

    /** Called before the application starts.
      *
      * Resources managed by plugins, such as database connections, are likely not available at this point.
      *
      */
    override def open(): Configuration = {
      super.open()
      log.debug("Scrupal startup initiated.")

      // Instantiate the core module and make sure that we registered it as 'Core

      val core = CoreModule()(this)
      require(Modules('Core).isDefined, "Failed to find the CoreModule as 'Core")
      core.bootstrap(_configuration)

      val configured_modules: Seq[String] = {
        // FIXME: can we avoid configuring the specific modules here?
        // How about JSR 367?  in JSE 9? https://www.jcp.org/en/jsr/detail?id=376
        _configuration.getStringSeq("scrupal.modules") match {
          case Some(list) ⇒ list
          case None ⇒ List.empty[String]
        }
      }

      // Now we go through the configured modules and bootstrap them
      for (class_name ← configured_modules) {
        scrupal.api.Scrupal.findModuleOnClasspath(class_name) match {
          case Some(module) ⇒ module.bootstrap(_configuration)
          case None ⇒ log.warn("Could not locate module with class name: " + class_name)
        }
      }

      // Load the configuration and wait at most 10 seconds for it
      val future = load(_configuration, _storeContext) map { sites ⇒
        if (sites.isEmpty)
          toss("Refusing to start because of load errors. Check logs for details.")
        else {
          log.info("Loaded Sites:\n" + sites.map { site ⇒ s"${site.label}" })
        }
        log.debug("Scrupal startup completed.")
        _configuration
      }
      Await.result(future, 10.seconds)
    }

    override def authenticate(rc: Context): Option[Principal] = None

    type FlatConfig = TreeMap[String, ConfigValue]

    def interestingConfig(config: Configuration): FlatConfig = {
      val elide: Regex = "^(akka|java|sun|user|awt|os|path|line).*".r
      val entries = config.entrySet.toSeq
      val filtered = entries filter { case (x, y) ⇒ !elide.findPrefixOf(x).isDefined }
      TreeMap[String, ConfigValue](filtered.toSeq: _*)
    }

    /** Load the Sites from configuration
      * Site loading is based on the database configuration. Whatever databases are loaded, they are scanned and any
      * sites in them are fetched and instantiated into the memory registry. Note that multiple sites may utilize the
      * same database information. We utilize this to open the database and load the site objects they contain
   *
      * @param config The Scrupal Configuration to use to determine the initial loading
      * @param context The database context from which to load the
      */
    protected def load(config: Configuration, context: StoreContext): Future[Seq[Site]] = {
      val coreSchema = CoreSchemaDesign()
      try {
        context.addSchema(coreSchema) flatMap { schema ⇒
          schema.collectionFor[Site]("sites") match {
            case Some(sitesCollection: Collection[Site]) ⇒ {
              val result = sitesCollection.fetchAll().map {
                sites ⇒ {
                  for (site ← sites) yield {
                    log.debug(s"Loading site '${site.name}' for host ${site.hostNames}, enabled=${site.isEnabled(this)}")
                    site.enable(this)
                    site
                  }
                }.toSeq
              }
              DataCache.update(this, schema)
              result
            }
            case None ⇒
              toss("Collection 'sites' was not found")
          }
        }
      } catch {
        case x: Throwable ⇒
          log.error("Attempt to validate core schema failed: ", x)
          throw x
      }
    }
}


object foo {



  val Sites = SitesRegistry()
  val Applications = ApplicationsRegistry()
  val Modules = ModulesRegistry()
  val Entities = EntitiesRegistry()
  val Features = FeaturesRegistry()

  def withStoreContext[T](f : StoreContext ⇒ T) : T = {
    f(_storeContext)
  }

  def withSchema[T](schemaName: String)(f : Schema ⇒ T) : T = _storeContext.withSchema[T](schemaName)(f)

  def withAssetsLocator[T](f : (AssetsLocator) ⇒ T) : T = {
    f(_assetsLocator)
  }

  /** Simple utility to determine if we are considered "ready" or not. Basically, if we have a non empty Site
    * Registry then we have had to found a database and loaded the sites. So that is our indicator of whether we
    * are configured yet or not.
    *
    * @return True iff there are sites loaded
    */
  def isReady : Boolean = _configuration.getConfig("scrupal").nonEmpty && Sites.nonEmpty

  def isChildScope(e : Enablement[_]) : Boolean = e match {
    case s : Site ⇒ Sites.containsValue(s)
    case _ ⇒ false
  }

  def authenticate(context : Context) : Option[Principal] = None

  /** Called before the application starts.
    *
    * Resources managed by plugins, such as database connections, are likely not available at this point.
    *
    */
  def open() : Configuration = {
    // Set up logging
    LoggingHelpers.initializeLogging(forDebug = true)

    // We do a lot of stuff in API objects and they need to be instantiated in the right order,
    // so "touch" them now because they are otherwise initialized randomly as used
    require(Types.registryName == "Types")
    require(Modules.registryName == "Modules")
    require(Sites.registryName == "Sites")
    require(Entities.registryName == "Entities")
    require(Template.registryName == "Templates")

    _configuration
  }

  def close() : Unit = {}

  /** Load the Sites from configuration
    * Site loading is based on the database configuration. Whatever databases are loaded, they are scanned and any
    * sites in them are fetched and instantiated into the memory registry. Note that multiple sites may utilize the
    * same database information. We utilize this to open the database and load the site objects they contain
    *
    * @param config The Scrupal Configuration to use to determine the initial loading
    * @param context The database context from which to load the
    */
  protected def load(config : Configuration, context : StoreContext) : Future[Seq[Site]]

  /** Handle A Reactor
    * This is the main entry point into Scrupal for processing actions. It very simply forwards the action to
    * the dispatcher for processing and (quickly) returns a future that will be completed when the dispatcher gets
    * around to it. The point of this is to hide the use of actors within Scrupal and have a nice, simple, quickly
    * responding synchronous call in order to obtain the Future to the eventual result of the action.
    *
    * @param reactor The reactor to act upon (a Request ⇒ Response function).
    * @return A Future to the eventual Response
    */
  def dispatch[CT](request: Stimulus, reactor : Reactor) : Future[Response] = {
    reactor(request)
  }

}

    */

