package fr.ramiro.gcache

import fr.ramiro.gcache.testfixture.CacheTestFixture
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

class GuavaCacheMonixTest extends CacheTestFixture[Task] {
  override def valueF[A](f: Task[A]): A = f.runSyncUnsafe()
}
