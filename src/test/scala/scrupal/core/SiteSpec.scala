package scrupal.core

import play.api.test.FakeRequest
import scrupal.test.{FakeSite, ScrupalSpecification}

class SiteSpec extends ScrupalSpecification("Site") {

  "Site" should {
    "register instances" in {
      val instance = new Site("foo")(scrupal)
      scrupal.sites.lookup('foo) must beEqualTo(Some(instance))
      instance.requireHttps must beFalse
    }
    "match its host properly" in {
      val site = new Site("fum","fum.com", "description")
      site.forHost("foo.com") must beFalse
      site.forHost("fum.com") must beTrue
      site.forHost("admin.fum.com") must beTrue
    }
    "provide reactors and handlers" in {
      val site = new Site("fie", "fie.com")
      val request = FakeRequest("GET", "/")
      site.reactorFor(request, "") must beEqualTo(None)
      site.handlerForRequest(request) must beEqualTo(request → null)
    }
  }

}

class SiteRegistrySpec extends ScrupalSpecification("SiteRegistry") {
  "SitesRegistry" should {
    "keep track of domain names" in {
      val site1 = FakeSite("foo","foo.com")(scrupal)
      val site2 = FakeSite("bar", "bar.com")(scrupal)
      scrupal.sites.unregister(site2)
      scrupal.sites.size must beEqualTo(1)
      scrupal.sites.registryName must beEqualTo("Sites")
      scrupal.sites.registrantsName must beEqualTo("site")
      scrupal.sites.forHost("bar.com") must beEqualTo(None)
      scrupal.sites.forHost("foo.com") must beEqualTo(Some(site1))
    }
  }
}
