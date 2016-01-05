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

import com.reactific.helpers.{Registrable, Registry}
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import scrupal.test.ScrupalSpecification

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class ProviderSpec extends ScrupalSpecification("Provider") {

  val provider1 = NullProvider('One)
  val provider2 = NullProvider('Two)

  case class NullProvider(id : Symbol) extends Provider {
    def provide : ReactionRoutes = {
      case null ⇒ NullReactor
    }
  }

  case object NullReactor extends Reactor {
    val description = "The Null Reactor"
    val oid : Option[Long] = None
    def apply(request: Stimulus): Future[NoopResponse.type] = Future.successful {NoopResponse}
  }

  "Provider" should {
    "have empty instance" in {
      Provider.empty.isInstanceOf[Provider] must beTrue
      val request = FakeRequest("GET", "/")
      Provider.empty.provide must beEqualTo(Provider.emptyReactionRoutes)
    }
  }
  "DelegatingProvider" should {
    val dp = new DelegatingProvider {
      def id: Identifier = 'DelegatingProvider
      def delegates: Iterable[Provider] = Seq(provider1, provider2)
    }
    "delegate" in {
      scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        val req: RequestHeader = null
        dp.isTerminal must beFalse
        val maybe_reaction = dp.provide.lift(req)
        maybe_reaction.isDefined must beTrue
        val reaction = maybe_reaction.get
        reaction must beEqualTo(NullReactor)
        val future = reaction(Stimulus.empty).map { resp ⇒ resp.disposition must beEqualTo(Received) }
        Await.result(future, 1.seconds)
      }
    }
    "find provides with prefix" in {
      scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        val req: RequestHeader = null
        dp.withPrefix("foo").provide.lift(req).isDefined must beFalse
        val maybe_reaction = dp.withPrefix("/").provide.lift(req)
        maybe_reaction.isDefined must beTrue
        val reaction = maybe_reaction.get
        reaction must beEqualTo(NullReactor)
        val future = reaction(Stimulus.empty).map { resp ⇒ resp.disposition must beEqualTo(Received) }
        Await.result(future, 1.seconds)
      }
    }
  }

  "IdentifiableProvider" should {
    val ip = new IdentifiableProvider { val id : Symbol = 'test
      override def provide: ReactionRoutes = PartialFunction.empty[RequestHeader,Reactor]
    }
    "include Provider in name" in {
      ip.toString must beEqualTo("Provider(test)")
    }
  }


  "EnablementProvider" should {
    object TERegistry extends Registry[TestEnablee] {
      def registryName: String = "TestRegistry"
      def registrantsName: String = "testees"
    }
    case class TestEnablee(id : Symbol, override val parent : Option[Enablee] = None)
      extends Enablee with Registrable[TestEnablee] with Provider {
      override def registry: Registry[TestEnablee] = TERegistry
      override def provide: ReactionRoutes = { case (req: RequestHeader) ⇒ UnimplementedReactor("single") }
    }
    case class TestEP(id: Identifier) extends EnablementProvider[TestEP] {
      override def isChildScope(scope: Enablement[_]): Boolean = true
      override def registry: Registry[_] = TERegistry
    }
    val e_root = TestEnablee('e_root)
    val e_root_1 = TestEnablee('e_root_1, Some(e_root))
    val e_root_2 = TestEnablee('e_root_2, Some(e_root))

    "provide Reactor for nested children" in {
      val request = FakeRequest("GET", "one/e_root/e_root1")
      val stimulus = Stimulus(SimpleContext(scrupal), request)
      val ep = new TestEP('one)
      ep.enable(e_root)
      ep.enable(e_root_1)
      ep.disable(e_root_2)
      val future = ep.provide(request)(stimulus).map { resp : Response[_] ⇒
        resp.disposition must beEqualTo(Unimplemented)
        resp.asInstanceOf[UnimplementedResponse].formatted must beEqualTo("Unimplemented: single")
      }
      await(future)
    }
  }

  "SingularProvider" should {
    val sp = new SingularProvider {
      def id: Identifier = 'foot$
      override def singularRoutes: ReactionRoutes = {
        case (r : RequestHeader) ⇒ UnimplementedReactor("single")
      }
    }

    "has isSingular cateogrize requests properly" in {
      sp.singularPrefix must beEqualTo("foot-")
      sp.isSingular(FakeRequest("GET", "foot-")) must beTrue
      sp.isSingular(FakeRequest("GET", "bar")) must beFalse
    }

    "find provides with prefix" in {
      scrupal.withExecutionContext { implicit ec: ExecutionContext ⇒
        val req: RequestHeader = null
        sp.withPrefix("foo").provide.lift(req).isDefined must beFalse
        sp.withPrefix("/foo").provide.lift(req).isDefined must beFalse
      }
    }


    "provides reactor appropriately" in {
      val request = FakeRequest("GET", "/foot-")
      val stimulus = Stimulus(SimpleContext(scrupal), request)
      val future = sp.provide(request)(stimulus).map { resp: Response[_] ⇒
        resp.disposition must beEqualTo(Unimplemented)
        resp.asInstanceOf[UnimplementedResponse].formatted must beEqualTo("Unimplemented: single")
      }
      await(future)
    }
  }

  "PluralProvider" should {
    val sp = new PluralProvider {
      def id: Identifier = 'foot
      override def singularRoutes: ReactionRoutes = {
        case (r : RequestHeader) ⇒ UnimplementedReactor("singularity")
      }
      override def pluralRoutes: ReactionRoutes = {
        case (r : RequestHeader) ⇒ UnimplementedReactor("plurality")
      }
    }

    "categorizes singular and plural requests separately" in {
      sp.singularPrefix must beEqualTo("foot")
      sp.pluralPrefix must beEqualTo("feet")
      val reqSingle = FakeRequest("GET", "foot")
      sp.isSingular(reqSingle) must beTrue
      sp.isPlural(reqSingle) must beFalse
      val reqPlural = FakeRequest("GET", "feet")
      sp.isSingular(reqPlural) must beFalse
      sp.isPlural(reqPlural) must beTrue
    }

    "provides reactor appropriate for plurality of request" in {
      val singleRequest = FakeRequest("GET", "/foot")
      val singleStimulus = Stimulus(SimpleContext(scrupal), singleRequest)
      val singleFuture = sp.provide(singleRequest)(singleStimulus).map { resp: Response[_] ⇒
        resp.disposition must beEqualTo(Unimplemented)
        resp.isInstanceOf[UnimplementedResponse]
        val ur = resp.asInstanceOf[UnimplementedResponse]
        ur.formatted must beEqualTo("Unimplemented: singularity")
      }
      val pluralRequest = FakeRequest("GET", "/feet")
      val pluralStimulus = Stimulus(SimpleContext(scrupal), pluralRequest)
      val pluralFuture = sp.provide(pluralRequest)(pluralStimulus).map { resp: Response[_] ⇒
        resp.disposition must beEqualTo(Unimplemented)
        resp.isInstanceOf[UnimplementedResponse]
        val ur = resp.asInstanceOf[UnimplementedResponse]
        ur.formatted must beEqualTo("Unimplemented: plurality")
      }
      await(singleFuture)
      await(pluralFuture)

    }
  }

}
