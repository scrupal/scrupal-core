package scrupal.core

import org.specs2.mutable.Specification
import play.api.Application
import scrupal.test.ScrupalCache
import scrupal.utils.ScrupalComponent

/** Test Case For Scrupal Application */
class ScrupalSpec extends Specification {

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
  }
}
