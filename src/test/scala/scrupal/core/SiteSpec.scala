package scrupal.core

import org.specs2.mutable.Specification

class SiteSpec extends Specification {

  "Site" should {
    "register instances" in {
      val instance = new Site("foo")
      Site.lookup('foo) must beEqualTo(Some(instance))
    }
  }
}
