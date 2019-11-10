package com.glyde

import cats.FlatMap
import cats.effect.Timer
import cats.implicits._
import fs2.kafka.{AdminClientSettings, ConsumerSettings}

import scala.concurrent.duration.FiniteDuration
import scala.language.higherKinds

package object exporter {

  implicit class IOps[F[_]](val task: F[Unit]) extends AnyVal {
    def repeatEvery(interval: FiniteDuration)(implicit timer: Timer[F], f: FlatMap[F]): F[Unit] =
      task >>= (_ => timer.sleep(interval)) >>= (_ => repeatEvery(interval))
  }

  implicit class ConsumerSettingsOps[F[_]](val settings: ConsumerSettings[F, Array[Byte], Array[Byte]]) extends AnyVal {
    def withSecurityConfig(maybeSecure: Option[SecurityConfig]): ConsumerSettings[F, Array[Byte], Array[Byte]] =
      maybeSecure.fold(settings)(sc => settings.withProperties(sc.toMap))
  }

  implicit class AdminClientSettingsOps[F[_]](val settings: AdminClientSettings[F]) extends AnyVal {
    def withSecurityConfig(maybeSecure: Option[SecurityConfig]): AdminClientSettings[F] =
      maybeSecure.fold(settings)(sc => settings.withProperties(sc.toMap))
  }

}
