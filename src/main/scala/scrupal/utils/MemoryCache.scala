package scrupal.utils

import java.util.concurrent.ConcurrentHashMap

import akka.actor.ActorRef

import scala.collection.JavaConverters._
import scala.language.postfixOps

/**
  * This trait makes a simple cache from a ConcurrentHashMap accompanied by a getOrElse function to obtain or create values
  * @tparam K    type of the keys
  * @tparam V    type of the values
  */
class MemoryCache[K,V] {

  private val cache = new ConcurrentHashMap[K,V] asScala

  def get(key : K) : Option[V] = cache.get(key)

  def getOrElse(key : K)(value: ⇒  V) : V = { cache.getOrElseUpdate(key, value) }

  def size : Int = cache.size

  def clear() : Unit = cache.clear()

  def replace(k:K, newV: V) : Option[V] = { cache.replace(k, newV) }

  def remove(k:K) : Option[V] = cache.remove(k)

}

object MemoryCache {
  def apply[K,V]() : MemoryCache[K,V] = new MemoryCache[K,V]
}

object ActorRefCache {
  def apply[K]() : MemoryCache[K,ActorRef] = new MemoryCache[K,ActorRef]
}
