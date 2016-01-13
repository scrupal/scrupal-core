package scrupal.core

import play.api.libs.json.JsString
import scala.concurrent.ExecutionContext.Implicits.global

import scrupal.test.ScrupalSpecification

class ContentSpec extends ScrupalSpecification("Content") {

  "EmptyContent" should {
    "have Unit content" in {
      EmptyContent.content must beEqualTo(())
    }
    "convert to empty bytes" in {
      val future = EmptyContent.toBytes.map { bytes ⇒
        bytes.isEmpty must beTrue
      }
      await(future)
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
    "convert to Html" in {
      val tc = ThrowableContent(mkThrowable("testing"))
      val html = tc.toHtml
      html.body.contains("testing") must beTrue
    }
    "convert to Text" in {
      val tc = ThrowableContent(mkThrowable("testing"))
      val text = tc.toTxt
      text.body.contains("testing") must beTrue
    }
    "convert to Bytes" in {
      val tc = ThrowableContent(mkThrowable("testing"))
      val future = tc.toBytes.map { bytes ⇒
        bytes.isEmpty must beFalse
        val str = new String(bytes, utf8)
        str.contains("testing") must beTrue
      }
      await(future)
    }
  }
}
