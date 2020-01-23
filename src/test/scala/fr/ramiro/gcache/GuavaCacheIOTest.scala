package fr.ramiro.gcache

import cats.effect.IO
import fr.ramiro.gcache.testfixture.CacheTestFixture

class GuavaCacheIOTest extends CacheTestFixture[IO] {
  override def valueF[A](f: IO[A]): A = f.unsafeRunSync()
}
