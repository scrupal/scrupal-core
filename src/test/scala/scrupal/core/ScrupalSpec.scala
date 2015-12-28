package scrupal.core

import org.specs2.mutable.Specification
import play.api.{Configuration, Application}
import play.api.mvc.Handler
import scrupal.test.{ScrupalSpecification, ScrupalCache}
import scrupal.utils.ScrupalComponent

import scala.concurrent.{ExecutionContextExecutorService, ExecutionContext, Future}

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

    /*
    "close cleanly" in {
      val scrupal = ScrupalCache("Close")
      scrupal.doClose() must beTrue
    }
    */

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
      val s4 = ScrupalCache("s3", additionalConfiguration = Map(
        "scrupal.executor.type" → "akka"
      ))
      val f4 = s4.withExecutionContext { implicit ec : ExecutionContext ⇒
        ec.isInstanceOf[ExecutionContextExecutorService] must beTrue
        Future { Thread.sleep(1) ; "s4" }
      }
      import scala.concurrent.ExecutionContext.Implicits.global
      val future = Future.sequence(Seq(f1, f2, f3, f4))
      await(future) must beEqualTo(Seq("s1", "s2", "s3", "s4"))
    }
  }
}
