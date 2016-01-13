package scrupal.core

import java.time.Instant

import play.api.test.FakeRequest
import scrupal.test.{FakeSite, ScrupalSpecification}

class SiteSpec extends ScrupalSpecification("Site") {

  "SiteData" should {
    "register instances" in {
      val instance = new SiteData("foo")
      instance.created must beEqualTo(Instant.EPOCH)
      instance.modified must beEqualTo(Instant.EPOCH)
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
    "provide reactors and handlers" in {
      val site = new Site(new SiteData("fie", "fie.com"))(scrupal)
      val request = FakeRequest("GET", "/")
      site.reactorFor(request, "") must beEqualTo(None)
      // site.handlerForRequest(request) must beEqualTo(request → null)
    }
  }

}

class SiteRegistrySpec extends ScrupalSpecification("SiteRegistry") {
  "SitesRegistry" should {
    "keep track of domain names" in {
      val site1 = FakeSite(SiteData("foo","foo.com"))(scrupal)
      val site2 = FakeSite(SiteData("bar", "bar.com"))(scrupal)
      scrupal.sites.unregister(site2)
      scrupal.sites.size must beEqualTo(1)
      scrupal.sites.registryName must beEqualTo("Sites")
      scrupal.sites.registrantsName must beEqualTo("site")
      scrupal.sites.forHost("bar.com") must beEqualTo(None)
      scrupal.sites.forHost("foo.com") must beEqualTo(Some(site1))
    }
  }
}
