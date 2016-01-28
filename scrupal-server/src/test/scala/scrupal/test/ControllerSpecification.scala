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

package scrupal.test

import com.reactific.helpers.LoggingHelper
import play.api.libs.iteratee.Iteratee
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.core._

import scala.concurrent.ExecutionContext

abstract class ControllerSpecification(name : String) extends ScrupalSpecification(name) {

  LoggingHelper.setToDebug(this)

  case class TestEntityProvider(id : Symbol) extends EntityProvider

  class SiteForControllerTest(implicit scrpl: Scrupal)
    extends Site(new SiteData(name="Foo", domainName="foo.com"))(scrpl) {
    override def reactorFor(request: RequestHeader) : Option[Reactor] = {
      val reactor = super.reactorFor(request)
      reactor
    }
    val tep1 = TestEntityProvider(Symbol("foot"))
    val tep2 = TestEntityProvider(Symbol("book"))
    enable(tep1,this)
    enable(tep2,this)
    require(delegates.toSeq.contains(tep1),"Failed to register tep1")
  }

  case class Case(method: String, url: String, expectedDisposition: Disposition, expectedBody: String)

  def testCases : Seq[Case]

  def makeSite(implicit scrupal : Scrupal) : Site = {
    new SiteForControllerTest()
  }

  def prepareDb(scrupal: Scrupal, schema : CoreSchema_H2) : Unit = {
    await(schema.create())
    await(
      schema.db.run {
        val site = makeSite(scrupal)
        schema.sites.create(site.data)
      }
    )
  }

  name should {
    "route all test paths correctly" in withScrupalSchema(name+"-paths") { (scrupal, schema) ⇒
      implicit val ec : ExecutionContext = scrupal.executionContext
      prepareDb(scrupal, schema)
      for ( Case(method,path,expDisp,expBody) ← testCases) {
        val req = FakeRequest(method, path).withHeaders("Host"→"foo.com")
        route(scrupal.application, req) match {
          case Some(fr) ⇒
            val result = await(fr)
            val future = result.body.run(Iteratee.consume[Array[Byte]]().map(x => new String(x, utf8))).map { body ⇒
              val expectedCode = expDisp.toStatusCode.intValue()
              if (result.header.status != expectedCode) {
                failure(s"Wrong status (${result.header.status}) for request ($req) expected ($expectedCode) with body($body) and expected body($expBody)")
              }
              body must contain(expBody)
            }
            await(future)
          case None ⇒
            failure("route not found")
        }
      }
      success
    }
  }
}
