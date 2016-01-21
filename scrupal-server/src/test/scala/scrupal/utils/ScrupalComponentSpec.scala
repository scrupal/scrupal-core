package scrupal.utils

import org.specs2.mutable.Specification

class ScrupalComponentSpec extends Specification {

  case class TestComponent() extends ScrupalComponent

  "ScrupalComponent" should {
    "toss ScrupalException" in {
      val comp = TestComponent() ;
      { comp.toss("test exception") ; 3 }  must throwA[ScrupalException]
    }
  }

}
