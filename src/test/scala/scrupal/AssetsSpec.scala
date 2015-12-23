package scrupal

import org.specs2.mutable.Specification
import play.api.http.DefaultHttpErrorHandler
import scrupal.router.Assets

class AssetsSpec extends Specification {

  def mkAssets : Assets = { new Assets(new DefaultHttpErrorHandler())}
  "Assets" should {
    "generate theme names" in {

    }
  }
}
