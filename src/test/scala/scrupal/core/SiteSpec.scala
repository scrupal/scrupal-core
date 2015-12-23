package scrupal.core

import scrupal.test.ScrupalSpecification

class SiteSpec extends ScrupalSpecification("Site") {

  "Site" should {
    "register instances" in {
      val instance = new Site("foo")(scrupal)
      scrupal.sites.lookup('foo) must beEqualTo(Some(instance))
    }
  }
}
