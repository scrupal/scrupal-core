package scrupal.core

import org.apache.commons.lang3.exception.ExceptionUtils
import play.api.libs.json.{JsString, Json}
import scrupal.test.ScrupalSpecification
import scrupal.utils.ScrupalException

class ContentSpec extends ScrupalSpecification("Content") {

  "EmptyContent" should {
    "have Unit content" in {
      EmptyContent.content must beEqualTo(())
    }
  }

  "ThrowableContent" should {
    "convert to JSON" in {
      val tc = ThrowableContent(mkThrowable("testing"))
      val json = tc.toJson
      json.keys.toSeq.sorted must beEqualTo(Seq("class", "message", "rootCauseMessage", "rootCauseStack", "stack"))
      val clazz = (json \ "class").get
      clazz.isInstanceOf[JsString] must beTrue
      clazz.asInstanceOf[JsString].value must beEqualTo("scrupal.utils.ScrupalException")
      val message = (json \ "message").get
      message.isInstanceOf[JsString] must beTrue
      message.asInstanceOf[JsString].value.contains("testing") must beTrue
    }
  }
}
