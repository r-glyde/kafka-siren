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
      task >> timer.sleep(interval) >> repeatEvery(interval)
  }

  implicit class ConsumerSettingsOps[F[_]](val settings: ConsumerSettings[F, Array[Byte], Array[Byte]]) extends AnyVal {
    def withSecurityConfig(securityConfig: SecurityConfig): ConsumerSettings[F, Array[Byte], Array[Byte]] =
      securityConfig.sslConfig.fold(settings)(ssl =>
        if (securityConfig.protocol.toLowerCase == "ssl") settings.withProperties(ssl.toMap) else settings)
  }

  implicit class AdminClientSettingsOps[F[_]](val settings: AdminClientSettings[F]) extends AnyVal {
    def withSecurityConfig(securityConfig: SecurityConfig): AdminClientSettings[F] =
      securityConfig.sslConfig.fold(settings)(ssl =>
        if (securityConfig.protocol.toLowerCase == "ssl") settings.withProperties(ssl.toMap) else settings)
  }

}
