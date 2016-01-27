/**********************************************************************************************************************
  * This file is part of Scrupal, a Scalable Reactive Web Application Framework for Content Management                 *
  *                                                                                                                    *
  * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
  *                                                                                                                    *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
  * with the License. You may obtain a copy of the License at                                                          *
  *                                                                                                                    *
  *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
  *                                                                                                                    *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
  * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
  * the specific language governing permissions and limitations under the License.                                     *
  **********************************************************************************************************************/
package scrupal.core

import java.io.ByteArrayInputStream
import java.util

import play.api.libs.iteratee.Enumerator
import play.api.libs.json.{Json, JsString}

import scrupal.test.ScrupalSpecification

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scalatags.Text.all._


class ContentSpec extends ScrupalSpecification("Content") {

  "Content" should {
    "convert to bytes" in {
      val data = Array[Byte](1,2,3,4,5)
      val enum = Enumerator(data)
      val ec = EnumeratedContent(enum)
      val f1 = ec.toBytes.map { bytes ⇒ util.Arrays.equals(bytes, data) must beTrue }
      val enum2 = Enumerator(data)
      val ec2 = EnumeratorsContent(Seq(enum, enum2))
      val f2 = ec2.toBytes.map { bytes ⇒
        bytes.length must beEqualTo(10)
        util.Arrays.equals(bytes.slice(0,5), data) must beTrue
        util.Arrays.equals(bytes.slice(5,10), data) must beTrue
      }
      val oc = OctetsContent(data)
      val f3 = oc.toBytes.map { bytes ⇒ util.Arrays.equals(bytes, data) must beTrue}
      val html = Seq(div("foo"))
      val hc = HtmlContent(html)
      val f4 = hc.toBytes.map { bytes ⇒ html.render must beEqualTo(new String(bytes,utf8)) }
      val is = new ByteArrayInputStream(data)
      val sc = StreamContent(is)
      val f5 = sc.toBytes.map { bytes ⇒ util.Arrays.equals(bytes, data) must beTrue}
      val js = Json.obj("foo" → "bar")
      val jc = JsonContent(js)
      val f6 = jc.toBytes.map { bytes ⇒ Json.stringify(js) must beEqualTo(new String(bytes,utf8)) }
      val f = Future.sequence(Seq(f1, f2, f3, f4, f5))
      await(f)
    }
  }

  "HtmlContent" should {
    "construct from scalatags RawFrag" in {
      val rawFrag : RawFrag = RawFrag("<div>foo</div>")
      val hc = HtmlContent(rawFrag)
      hc.content.render must beEqualTo(div("foo").render)
    }
  }

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
      html.render.contains("testing") must beTrue
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
