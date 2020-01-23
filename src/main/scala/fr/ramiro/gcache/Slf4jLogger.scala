package fr.ramiro.gcache

import cats.effect.Sync
import cats.implicits._
import org.slf4j.{LoggerFactory, Logger => JLogger}

class Slf4jLogger[F[_]](val logger: JLogger)(implicit F: Sync[F]) {
  def isTraceEnabled: F[Boolean] = F.delay(logger.isTraceEnabled)
  def isDebugEnabled: F[Boolean] = F.delay(logger.isDebugEnabled)
  def isInfoEnabled: F[Boolean]  = F.delay(logger.isInfoEnabled)
  def isWarnEnabled: F[Boolean]  = F.delay(logger.isWarnEnabled)
  def isErrorEnabled: F[Boolean] = F.delay(logger.isErrorEnabled)

  def trace[A](msg: A => String)(a: A): F[A] =
    isTraceEnabled.ifM(F.delay(logger.trace(msg(a))), F.unit) *> a.pure[F]
  def trace(msg: => String): F[Unit] =
    isTraceEnabled.ifM(F.delay(logger.trace(msg)), F.unit)
  def trace(msg: => String, t: Throwable): F[Unit] =
    isTraceEnabled.ifM(F.delay(logger.trace(msg, t)), F.unit)

  def debug[A](msg: A => String)(a: A): F[A] =
    isDebugEnabled.ifM(F.delay(logger.debug(msg(a))), F.unit) *> a.pure[F]
  def debug(msg: => String): F[Unit]               = isDebugEnabled.ifM(F.delay(logger.debug(msg)), F.unit)
  def debug(msg: => String, t: Throwable): F[Unit] = isDebugEnabled.ifM(F.delay(logger.debug(msg, t)), F.unit)

  def info[A](msg: A => String)(a: A): F[A] =
    isInfoEnabled.ifM(F.delay(logger.info(msg(a))), F.unit) *> a.pure[F]
  def info(msg: => String): F[Unit]               = isInfoEnabled.ifM(F.delay(logger.info(msg)), F.unit)
  def info(msg: => String, t: Throwable): F[Unit] = isInfoEnabled.ifM(F.delay(logger.info(msg, t)), F.unit)

  def warn[A](msg: A => String)(a: A): F[A] =
    isWarnEnabled.ifM(F.delay(logger.warn(msg(a))), F.unit) *> a.pure[F]
  def warn(msg: => String): F[Unit]               = isWarnEnabled.ifM(F.delay(logger.warn(msg)), F.unit)
  def warn(msg: => String, t: Throwable): F[Unit] = isWarnEnabled.ifM(F.delay(logger.warn(msg, t)), F.unit)

  def error[A](msg: A => String)(a: A): F[A] =
    isErrorEnabled.ifM(F.delay(logger.error(msg(a))), F.unit) *> a.pure[F]
  def error(msg: => String): F[Unit]               = isErrorEnabled.ifM(F.delay(logger.error(msg)), F.unit)
  def error(msg: => String, t: Throwable): F[Unit] = isErrorEnabled.ifM(F.delay(logger.error(msg, t)), F.unit)
}

object Slf4jLogger {
  def apply[F[_]: Sync](loggerName: String)  = new Slf4jLogger(LoggerFactory.getLogger(loggerName))
  def apply[F[_]: Sync](underlying: JLogger) = new Slf4jLogger(underlying)
}
