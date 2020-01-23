package fr.ramiro.gcache.testfixture

import cats.effect.Sync
import cats.implicits._
import fr.ramiro.gcache.GuavaCache
import org.scalamock.scalatest.MockFactory
import org.scalatest.OptionValues
import org.scalatest.funsuite.AnyFunSuite

abstract class CacheTestFixture[F[_]: Sync] extends AnyFunSuite with OptionValues with MockFactory {

  def valueF[A](f: F[A]): A

  private val cache = GuavaCache[F, String]

  val data = Map(
    "key1" -> "value1",
    "key2" -> "value2"
  )

  test("put-get-remove") {
    val (key, value) = data.head

    valueF {
      for {
        _       <- cache.put(key)(value)
        actual  <- cache.get(key)
        _       <- cache.remove(key)
        deleted <- cache.get(key)
      } yield {
        assert(actual.value == value)
        assert(deleted.isEmpty)
      }
    }
  }

  test("put-get-removeAll") {
    valueF {
      for {
        _       <- data.map { case (key, value) => cache.put(key)(value) }.toList.sequence
        actual  <- data.keys.map(cache.get).toList.sequence.map(_.flatten.toSet)
        _       <- cache.removeAll
        deleted <- data.keys.map(cache.get).toList.sequence.map(_.flatten.toSet)
      } yield {
        assert(actual == data.values.toSet)
        assert(deleted.isEmpty)
      }
    }
  }

  test("cachingF") {
    val (key, value) = data.head

    val f = mockFunction[String]
    f.expects().once().returning(value)
    val actual = valueF {
      cache.removeAll *> (0 until 10).foldLeft[F[String]]("".pure[F])((prev, _) =>
        prev *> cache.cachingF(key)(f().pure[F])
      )
    }
    assert(actual == value)
  }

  test("caching") {
    val (key, value) = data.head

    val f = mockFunction[String]
    f.expects().once().returning(value)
    val actual = valueF {
      cache.removeAll *> (0 until 10).foldLeft[F[String]]("".pure[F])((prev, _) => prev *> cache.caching(key)(f()))
    }
    assert(actual == value)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  val nullKey = null

  test("get-error") {
    val actual = valueF {
      cache.get(nullKey)
    }
    assert(actual.isEmpty)
  }

  test("pull-error") {
    val value = "test"
    val actual = valueF {
      cache.put(nullKey)("test")
    }
    assert(actual == value)
  }

}
