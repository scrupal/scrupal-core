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
import play.api.Configuration
import scrupal.test.ScrupalSpecification
import scrupal.test.FakeSite

import scala.concurrent.ExecutionContext

/**
 * Created by reid on 11/11/14.
 */
class ContextSpec extends ScrupalSpecification("Context") {

  "Context" should {
    "construct a SimpleContext with a Scrupal" in {
      val ctxt = Context(scrupal)
      ctxt.isInstanceOf[SimpleContext] must beTrue
      ctxt.siteName must beEqualTo("<NoSite>")
    }
    "construct a SiteContext with a Site and a Scrupal" in {
      val site : FakeSite = FakeSite(new SiteData("foo", "foo.com"))(scrupal)
      val ctxt = Context(scrupal, site)
      ctxt.isInstanceOf[SiteContext] must beTrue
      ctxt.site.isDefined must beTrue
      ctxt.site.get must beEqualTo(site)
      ctxt.site.get.data.name must beEqualTo("foo")
    }
    "provides context features" in {
      val ctxt = Context(scrupal)
      ctxt.withConfiguration { config ⇒ config.isInstanceOf[Configuration] must beTrue ; 3 } must beEqualTo(3)
      ctxt.withExecutionContext { ec ⇒ ec.isInstanceOf[ExecutionContext] must beTrue ; 2 } must beEqualTo(2)
      ctxt.withActorSystem { as ⇒ as.isInstanceOf[ActorSystem] must beTrue ; 1 } must beEqualTo(1)
    }
    "empty gives NotImplementedError when accessing scrupal" in {
      val ctxt = Context.empty
      ctxt.scrupal must throwA[NotImplementedError]
    }
  }
}
