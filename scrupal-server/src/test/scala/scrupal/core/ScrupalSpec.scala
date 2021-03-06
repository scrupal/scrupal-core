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

import play.api.test.FakeRequest
import scrupal.test.{ScrupalSpecification, ScrupalCache}
import scrupal.utils.{ScrupalException, ScrupalComponent}

import scala.concurrent.{ExecutionContextExecutor, ExecutionContextExecutorService, ExecutionContext, Future}

import scrupal.html._

/** Test Case For Scrupal Application */
class ScrupalSpec extends ScrupalSpecification("Scrupal") {

  "Scrupal" should {
    "construct from ScrupalCache" in withScrupal("ConstructFromCache") { scrpl : Scrupal ⇒
      scrpl.isInstanceOf[Scrupal] must beTrue
      scrpl.isInstanceOf[ScrupalComponent] must beTrue
    }

    "is closeable" in withScrupal("IsCloseable") { scrupal ⇒
      scrupal.close()
      scrupal.application.actorSystem.isTerminated must beTrue
      scrupal.actorSystem.isTerminated must beTrue
    }

    "auto register" in withScrupal("AutoRegister") { scrpl : Scrupal ⇒
        Scrupal.lookup(Symbol("AutoRegister")) must beEqualTo(Some(scrpl))
    }

    "can access actor, exec, timeout" in withScrupal("Access") { scrpl ⇒
      scrpl.withActorExec { (as, ec, to) ⇒
        scrpl.actorSystem must beEqualTo(as)
        scrpl.executionContext must beEqualTo(ec)
        scrpl.akkaTimeout must beEqualTo(to)
      }
    }

    "have appropriately named registry" in {
      Scrupal.registrantsName must beEqualTo("scrupali")
      Scrupal.registryName must beEqualTo("Scrupalz")
    }

    "allow thread pool to be configured" in {
      val f1 = withScrupal("s1", moreConfig = Map("scrupal.executor.type" → "fixed-thread-pool")) { s1: Scrupal ⇒
        s1.withExecutionContext { implicit ec: ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          val future = Future { "s1"}
          await(future)
        }
      }
      f1 must beEqualTo("s1")

      val f2 = withScrupal("s2", moreConfig = Map("scrupal.executor.type" → "default")) { s2 : Scrupal ⇒
        s2.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          val future = Future { "s2" }
          await(future)
        }
      }
      f2 must beEqualTo("s2")

      val f3 = withScrupal("s3", moreConfig = Map("scrupal.executor.type" → "thread-pool")) { s3 : Scrupal ⇒
        s3.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          val future = Future { "s3" }
          await(future)
        }
      }
      f3 must beEqualTo("s3")

      val f4 = withScrupal("s4", moreConfig = Map("scrupal.executor.type" → "akka")) { s4 : Scrupal ⇒
        s4.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutor] must beTrue
          val future = Future { "s4" }
          await(future)
        }
      }
      f4 must beEqualTo("s4")

      val f5 = withScrupal("s5", moreConfig = Map("scrupal.executor.type" → "work-stealing-pool")) { s1: Scrupal ⇒
        s1.withExecutionContext { implicit ec: ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          val future = Future { "s5"}
          await(future)
        }
      }
      f5 must beEqualTo("s5")

      val cfg = Map("scrupal.executor.type" → "not-a-pool-type")

      { withScrupal("s6", moreConfig = cfg) { s6 : Scrupal ⇒ () } } must
        throwA[ScrupalException]("Invalid value for configuration key 'scrupal.executor.type'")

    }

    "provides default site" in withScrupal("default_site") { scrupal ⇒
      val site = scrupal.localHostSite
      val req = FakeRequest("GET", "/")
      site.helpProvider.id must beEqualTo('help)
      site.adminProvider.id must beEqualTo('admin)

      site.reactorFor(req) match {
        case Some(reactor) ⇒
          val stimulus = Stimulus(Context(scrupal), req)
          val future = reactor(stimulus).map { response ⇒
            response.disposition must beEqualTo(Successful)
            response.payload.isInstanceOf[HtmlContent]
            val content = response.payload.asInstanceOf[HtmlContent]
            content.content.render.contains("scrupal-jsapp") must beTrue
          }(scrupal.executionContext)
          await(future)
          success
        case None ⇒
          failure("DefaultLocalHostSite should have provided Reactor")
      }
    }

    "siteForRequest works for subdomains" in withScrupal("subdomain_check") { scrupal ⇒
      val req = FakeRequest("GET", "/").withHeaders("Host" → "foo.bar.com")

      scrupal.siteForRequest(req) must beEqualTo(None)

      val site = new Site(SiteData("foo", "foo.bar.com", "nada"))(scrupal)

      scrupal.siteForRequest(req) must beEqualTo(Some(site))
    }
  }

  "ScrupalLoader" should {
    "load a Scrupal" in {
      val loader = new ScrupalLoader
      val (context, name) = ScrupalCache.makeContext("LoadedScrupal")
      val loaded_scrupal = loader.load(context)
      loaded_scrupal.actorSystem.name must beEqualTo("LoadedScrupal")
    }
  }
}
