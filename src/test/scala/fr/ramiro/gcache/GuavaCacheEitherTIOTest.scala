package fr.ramiro.gcache

import cats.data.EitherT
import cats.effect.IO
import fr.ramiro.gcache.testfixture.CacheTestFixture

class GuavaCacheEitherTIOTest extends CacheTestFixture[EitherT[IO, Throwable, *]] { //({type EitherTIO[T] = EitherT[IO, Throwable, T]})#EitherTIO
  override def valueF[A](f: EitherT[IO, Throwable, A]): A = f.value.unsafeRunSync().getOrElse(fail("should be right"))
}
