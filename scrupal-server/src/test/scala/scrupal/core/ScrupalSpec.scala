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
import scrupal.utils.ScrupalComponent

import scala.concurrent.{ExecutionContextExecutor, ExecutionContextExecutorService, ExecutionContext, Future}

import scrupal.html._

/** Test Case For Scrupal Application */
class ScrupalSpec extends ScrupalSpecification("Scrupal") {

  "Scrupal" should {
    "construct from ScrupalCache" in {
      val scrupal = ScrupalCache("ConstructFromCache")
      scrupal.isInstanceOf[Scrupal] must beTrue
      scrupal.isInstanceOf[ScrupalComponent] must beTrue
    }

    "auto register" in {
      val scrupal = ScrupalCache("AutoRegister")
      Scrupal.lookup(Symbol("AutoRegister")) must beEqualTo(Some(scrupal))
    }

    "close cleanly" in {
      val scrupal = ScrupalCache("Close")
      scrupal.doClose() must beTrue
    }

    "can access actor, exec, timeout" in {
      val scrupal = ScrupalCache("Access")
      scrupal.withActorExec { (as, ec, to) ⇒
        scrupal.actorSystem must beEqualTo(as)
        scrupal.executionContext must beEqualTo(ec)
        scrupal.akkaTimeout must beEqualTo(to)
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
          Future {Thread.sleep(1); "s1"}
        }
      }

      val f2 = withScrupal("s2", moreConfig = Map("scrupal.executor.type" → "default")) { s2 : Scrupal ⇒
        s2.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          Future { Thread.sleep(1) ; "s2" }
        }
      }
      val f3 = withScrupal("s3", moreConfig = Map("scrupal.executor.type" → "thread-pool")) { s3 : Scrupal ⇒
        s3.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
          Future { Thread.sleep(1) ; "s3" }
        }
      }
      val f4 = withScrupal("s4", moreConfig = Map("scrupal.executor.type" → "akka")) { s4 : Scrupal ⇒
        s4.withExecutionContext { implicit ec : ExecutionContext ⇒
          ec.isInstanceOf[ExecutionContextExecutor] must beTrue
          Future { Thread.sleep(1) ; "s4" }
        }
      }
      import scala.concurrent.ExecutionContext.Implicits.global
      val future = Future.sequence(Seq(f1, f2, f3, f4))
      await(future) must beEqualTo(Seq("s1", "s2", "s3", "s4"))
    }

    "provides default site" in {
      val site = scrupal.DefaultLocalHostSite
      val req = FakeRequest("GET", "/")
      site.reactorFor(req) match {
        case Some(reactor) ⇒
          val stimulus = Stimulus(context, req)
          val future = reactor(stimulus).map { response ⇒
            response.disposition must beEqualTo(Successful)
            response.payload.isInstanceOf[HtmlContent]
            val content = response.payload.asInstanceOf[HtmlContent]
            content.content.render.contains("react-polymer-app") must beTrue
          }(scrupal.executionContext)
          await(future)
          success
        case None ⇒
          failure("DefaultLocalHostSite should have provided Reactor")
      }
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
