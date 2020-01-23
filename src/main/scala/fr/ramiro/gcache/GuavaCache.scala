package fr.ramiro.gcache

import cats.effect.Sync
import cats.implicits._
import com.google.common.cache.{Cache, CacheBuilder}

import scala.util.control.NonFatal

class GuavaCache[F[_], V](val underlying: Cache[String, V])(implicit F: Sync[F]) {
  private val logger = Slf4jLogger[F](getClass.getName)

  def get(key: String): F[Option[V]] =
    (
      F.delay(Option(underlying.getIfPresent(key))) >>=
        logger.debug(result => s"Cache ${result.map(_ => "hit") getOrElse "miss"} for key $key")
    ).recoverWith { case NonFatal(e) => logger.warn(s"Failed to read from cache. Key = $key", e) *> none[V].pure[F] }

  def put(key: String)(value: V): F[V] =
    (
      F.delay({ underlying.put(key, value); value }) <* logger.debug(s"Inserted value into cache with key $key")
    ).recoverWith { case NonFatal(e) => logger.warn(s"Failed to write to cache. Key = $key", e) *> value.pure[F] }

  def remove(key: String): F[Unit] = F.delay(underlying.invalidate(key))

  def removeAll: F[Unit] = F.delay(underlying.invalidateAll())

  def caching(key: String)(f: => V): F[V] = cachingF(key)(F.delay(f))

  def cachingF(key: String)(f: => F[V]): F[V] = get(key) >>= (_.fold(f >>= put(key))(_.pure[F]))
}

object GuavaCache {
  def apply[F[_]: Sync, V <: AnyRef]: GuavaCache[F, V]                     = apply(CacheBuilder.newBuilder().build[String, V])
  def apply[F[_]: Sync, V](underlying: Cache[String, V]): GuavaCache[F, V] = new GuavaCache[F, V](underlying)
}
