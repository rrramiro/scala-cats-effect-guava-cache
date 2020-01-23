package fr.ramiro.gcache

import fr.ramiro.gcache.testfixture.CacheTestFixture
import zio.interop.catz._
import zio.{DefaultRuntime, Task}

class GuavaCacheZIOTest extends CacheTestFixture[Task] with DefaultRuntime {
  override def valueF[A](f: Task[A]): A = unsafeRun(f)
}
