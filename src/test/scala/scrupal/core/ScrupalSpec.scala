package scrupal.core

import play.api.test.FakeRequest
import scrupal.test.{ScrupalSpecification, ScrupalCache}
import scrupal.utils.ScrupalComponent

import scala.concurrent.{ExecutionContextExecutor, ExecutionContextExecutorService, ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/** Test Case For Scrupal Application */
class ScrupalSpec extends ScrupalSpecification("Scrupal") {

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

    "close cleanly" in {
      val scrupal = ScrupalCache("Close")
      scrupal.doClose() must beTrue
    }

    "can access actor, exec, timeout" in {
      val scrupal = ScrupalCache("Access")
      scrupal.withActorExec { (as, ec, to) ⇒
        scrupal.actorSystem must beEqualTo(as)
        scrupal.executionContext must beEqualTo(ec)
        scrupal.akkaTimeout must beEqualTo(to)
      }
    }

    "have appropriately named registry" in {
      Scrupal.registrantsName must beEqualTo("scrupali")
      Scrupal.registryName must beEqualTo("Scrupalz")
    }

    "allow thread pool to be configured" in {
      val s1 = ScrupalCache("s1", additionalConfiguration=Map(
        "scrupal.executor.type" → "fixed-thread-pool"
      ))
      val f1 = s1.withExecutionContext { implicit ec : ExecutionContext ⇒
        ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
        Future { Thread.sleep(1) ; "s1" }
      }
      val s2 = ScrupalCache("s2", additionalConfiguration = Map(
        "scrupal.executor.type" → "default"
      ))
      val f2 = s2.withExecutionContext { implicit ec : ExecutionContext ⇒
        ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
        Future { Thread.sleep(1) ; "s2" }
      }
      val s3 = ScrupalCache("s3", additionalConfiguration = Map(
        "scrupal.executor.type" → "thread-pool"
      ))
      val f3 = s3.withExecutionContext { implicit ec : ExecutionContext ⇒
        ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
        Future { Thread.sleep(1) ; "s3" }
      }
      val s4 = ScrupalCache("s4", additionalConfiguration = Map(
        "scrupal.executor.type" → "akka"
      ))
      val f4 = s4.withExecutionContext { implicit ec : ExecutionContext ⇒
        ec.isInstanceOf[ExecutionContextExecutor] must beTrue
        Future { Thread.sleep(1) ; "s4" }
      }
      import scala.concurrent.ExecutionContext.Implicits.global
      val future = Future.sequence(Seq(f1, f2, f3, f4))
      await(future) must beEqualTo(Seq("s1", "s2", "s3", "s4"))
    }

    "provides default site" in {
      val site = scrupal.DefaultLocalHostSite
      val req = FakeRequest("GET", "/")
      site.reactorFor(req) match {
        case Some(reactor) ⇒
          val stimulus = Stimulus(context, req)
          val future = reactor(stimulus).map { response ⇒
            response.disposition must beEqualTo(Successful)
            response.payload.isInstanceOf[HtmlContent]
            val content = response.payload.asInstanceOf[HtmlContent]
            content.content.body.contains("Welcome to Scrupal") must beTrue
          }
          await(future)
          success
        case None ⇒
          failure("DefaultLocalHostSite should have provided Reactor")
      }
    }
  }
  "ScrupalLoader" should {
    "load a Scrupal" in {
      val loader = new ScrupalLoader
      val (context, name) = ScrupalCache.makeContext("LoadedScrupal")
      val loaded_scrupal = loader.load(context)
      loaded_scrupal.actorSystem.name must beEqualTo("LoadedScrupal")
    }
  }
}
