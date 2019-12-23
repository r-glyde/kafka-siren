package com.glyde

import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Timer}
import fs2.kafka._

import scala.language.higherKinds

package object exporter {

  def withAdminClientAndConsumer[T, F[_] : Concurrent : ContextShift : Timer : ConcurrentEffect](
      config: ExporterConfig)(f: (KafkaAdminClient[F], KafkaConsumer[F, Array[Byte], Array[Byte]]) => F[T]): F[T] = {

    val adminSettings = AdminClientSettings[F]
      .withBootstrapServers(config.kafka.bootstrapServers)
      .withSecurityConfig(config.kafka.security)
    val consumerSettings = ConsumerSettings[F, Array[Byte], Array[Byte]]
      .withBootstrapServers(config.kafka.bootstrapServers)
      .withGroupId("kafka-exporter")
      .withSecurityConfig(config.kafka.security)

    adminClientResource(adminSettings).use { adminClient =>
      consumerResource(consumerSettings).use { consumer =>
        f(adminClient, consumer)
      }
    }
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
