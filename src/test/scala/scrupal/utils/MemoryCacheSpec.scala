package scrupal.utils

import java.util.concurrent.{Future, Executors, ExecutorService}

import akka.actor.ActorRef
import org.specs2.mutable.Specification

/** Test Case For Memory Cache */
class MemoryCacheSpec extends Specification {

  case class TestCache() extends MemoryCache[Int,Int]

  "MemoryCache" should {
    "has stable size" in {
      val cache = TestCache()
      cache.getOrElse(1)(1)
      cache.size must beEqualTo(1)
      cache.getOrElse(2)(2)
      cache.size must beEqualTo(2)
    }

    "has no size when cleared" in {
      val cache = TestCache()
      cache.getOrElse(1)(1)
      cache.size must beEqualTo(1)
      cache.clear()
      cache.size must beEqualTo(0)
    }

    "permits removal" in {
      val cache = TestCache()
      cache.getOrElse(1)(1)
      cache.getOrElse(2)(2)
      cache.getOrElse(3)(3)
      cache.remove(2) must beEqualTo(Some(2))
      cache.size must beEqualTo(2)
    }

    "permits replacement" in {
      val cache = TestCache()
      cache.getOrElse(1)(1)
      cache.getOrElse(2)(2)
      cache.replace(1,0)
      cache.size must beEqualTo(2)
      cache.get(1) must beEqualTo(Some(0))
    }

    "permits case class like construction" in {
      val cache = MemoryCache[Int,Int]()
      cache.getOrElse(1)(1)
      cache.size must beEqualTo(1)
      cache.get(1) must beEqualTo(Some(1))
    }

    "has ActorRef constructor" in {
      val cache = ActorRefCache[Int]()
      cache.isInstanceOf[MemoryCache[Int,ActorRef]] must beTrue
    }

    "must have no lock contention for readers" in {
      val cache = TestCache()
      cache.getOrElse(1)(1)
      cache.getOrElse(2)(2)
      cache.getOrElse(3)(3)
      cache.getOrElse(4)(4)
      cache.getOrElse(5)(5)
      cache.getOrElse(6)(6)
      cache.getOrElse(7)(7)
      cache.size must beEqualTo(7)

      val exec : ExecutorService  = Executors.newFixedThreadPool(100)

      case class Reader(num : Int) extends Runnable {
        def run() : Unit = {
          for (i <- 1 to 1000) {
            cache.get((Math.random * 7).toInt + 1)
          }
        }
      }

      val futures : IndexedSeq[Future[_]] = {
        for (i <- 1 to 100) yield {
          exec.submit( new Reader(i) )
        }
      }

      var count = 0
      for (i <- 0 to 99) {
        if (futures(i).get() == null) count += 1
      }

      count must beEqualTo(100)
    }

  }
}
