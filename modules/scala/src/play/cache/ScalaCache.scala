package play.cache

import play.libs.Time._
import scala.actors.Actor._
import scala.actors._

/**
 * extends the Cache API with two scala specific methods, this is made public via type alias
 */

private[cache] object ScalaCache extends CacheDelegate {

  private def prefixed(key: String) = "__" + key

  private val cacheActor =
    actor{
      link{self.trapExit = true;loop{react{case Exit(from: Actor, exc: Exception) => 
        {
          play.Logger.trace("cache actor crashed on: "+exc.toString+" resstarting...");
          from.restart();
          play.Logger.trace("restarted cache actor")}}}}
      loop{
        react{
          case (f: Function0[_]) => reply(f.asInstanceOf[Function0[Any]]())
          case _ => None
        }
      }
    }

  private def getFromCache[T](key: String) = Option(_impl.get(key).asInstanceOf[T])

  private def getFromCache1(key: String): Option[_] = Option(_impl.get(key))

  /**
   *  retrieves value from Cache based on the type parameter
   * @param key the name of the cache key
   * @param return either the value or None
   */

  def get[T](key: String)(implicit m: ClassManifest[T]): Option[T] = {
    if (key == null) None
    val v = _impl.get(key).asInstanceOf[T]
    if (v == null) None
    else if (m.erasure.isAssignableFrom(v.asInstanceOf[AnyRef].getClass)) {
      Some(v)
    } else {
      play.Logger.warn("Found a value in cache for key '%s' of type %s where %s was expected", key, v.asInstanceOf[AnyRef].getClass.getName, m.erasure.getName)
      None
    }
  }


  /**
   *  retrieves value from Cache based on the type parameter
   * @param key the name of the cache key
   * @param return either the value or None
   * @param expiration expiration period
   */

  def get[T](key: String, expiration: String)(getter: => T): T = {

    import play.libs.Time._

    get(key) match {
      case Some(x) => x
      case None => val r = getter
      set(key, r, expiration)
      r
    }
  }


  object Instances {
    implicit def isDesirable[A](o: Option[A]): Boolean = o.isDefined

    implicit def isDesirableSeq[A, B[X] <: Seq[X]](seq: B[A]): Boolean = seq.nonEmpty
  }

  /**
   * retrieves a key in async fashion
   * @param key cache key
   * @param expiration experiation period
   * @param window
   * @param waitForEvaluation
   * @return parameterized type
   */

  def getAsync[A](key: String, expiration: String, window: String, waitForEvaluation: String = "10s")(getter: => A)(implicit isDesirable: A => Boolean): A = {
    def scheduleOrIgnore(key: String, f: Function0[A]) = {
      val flagWhileCaching = "___" + key

      case class Caching()

      getFromCache1(flagWhileCaching).getOrElse
          {
            cacheActor.!!(() => {set(flagWhileCaching, Caching(), waitForEvaluation); f()}, {
              case a => {cacheIt(a.asInstanceOf[A]); _impl.delete(flagWhileCaching); a}
            })
          }
    }
    def cacheIt(t: => A) = get(key, expiration, window)(t)(isDesirable)

    getFromCache[A](key).getOrElse(
      getFromCache[A](prefixed(key)).map(v => {scheduleOrIgnore(key, () => getter); v})
        .getOrElse(cacheIt(getter)))

  }

  /**
   * retrieves key if it's not in cache it populates cache using the the passed in getter
   * @param key
   * @param experiation
   * @param window
   * @param getter
   * @return parameterized type
   */

  def get[A](key: String, expiration: String, window: String)(getter: => A)(implicit isDesirable: A => Boolean): A = {
    val cacheIt = (v: A) => {
      set(prefixed(key), v, parseDuration(expiration) + parseDuration(window) + "s")
      set(key, v, expiration)
      v
    }
    get(key).getOrElse({
      val result = getter;
      if (isDesirable(result)) cacheIt(result)
      else get(prefixed(key)).getOrElse(result)
    })
  }
}
