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

import java.time.Instant

import play.api.test.FakeRequest
import scrupal.test.{SharedTestScrupal, FakeSite, ScrupalSpecification}

class SiteSpec extends ScrupalSpecification("Site") {

  "SiteData" should {
    "register instances" in {
      val now = Instant.now()
      val instance = new SiteData("foo", created=now, modified=now)
      instance.created must beEqualTo(now)
      instance.modified must beEqualTo(now)
      instance.name must beEqualTo("foo")
      instance.name must be("foo")
      instance.domainName must be("localhost")
      instance.requireHttps must beFalse
      instance.description must beEmpty
      instance.oid must beNone
    }
    "match its host properly" in {
      val site = new SiteData("fum", "fum.com", "description")
      site.forHost("foo.com") must beFalse
      site.forHost("fum.com") must beTrue
      site.forHost("admin.fum.com") must beTrue
    }
  }
  "Site" should {
    "provide reactors and handlers" in withScrupal("reactors_and_handlers") { scrupal ⇒
      val site = new Site(new SiteData("fie", "fie.com"))(scrupal)
      val request = FakeRequest("GET", "/")
      site.reactorFor(request) must beEqualTo(None)
      // site.handlerForRequest(request) must beEqualTo(request → null)
    }
  }

}

class SiteRegistrySpec extends ScrupalSpecification("SiteRegistry") with SharedTestScrupal {
  "SitesRegistry" should {
    val site1 = FakeSite(SiteData("foo","foo.com"))(scrupal)
    val site2 = FakeSite(SiteData("bar", "bar.com"))(scrupal)
    val site3 = FakeSite(SiteData("wwwbar", "www.bar.com"))(scrupal)
    "keep track of domain names" in {
      scrupal.sites.size must beEqualTo(4) // DefaultLocalHostSite is always registered
      scrupal.sites.unregister(site2)
      scrupal.sites.size must beEqualTo(3)
      scrupal.sites.registryName must beEqualTo("Sites")
      scrupal.sites.registrantsName must beEqualTo("site")
      scrupal.sites.forHost("bar.com") must beEqualTo(None)
      scrupal.sites.forHost("foo.com") must beEqualTo(Some(site1))
      scrupal.sites.forHost("www.bar.com") must beEqualTo(Some(site3))
      scrupal.sites.forHost("localhost") must beEqualTo(Some(scrupal.DefaultLocalHostSite))
    }
  }
}
