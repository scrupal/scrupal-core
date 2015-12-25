package scrupal.core

import scrupal.test.{FakeSite, ScrupalSpecification}

class SiteSpec extends ScrupalSpecification("Site") {

  "Site" should {
    "register instances" in {
      val instance = new Site("foo")(scrupal)
      scrupal.sites.lookup('foo) must beEqualTo(Some(instance))
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
