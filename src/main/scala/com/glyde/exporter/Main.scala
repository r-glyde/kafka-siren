package com.glyde.exporter

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import fs2.kafka._
import kamon.Kamon
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Main extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    Kamon.loadModules()

    val config = ConfigSource.default.loadOrThrow[Config]
    logger.info(s"Initialised kafka-exporter with configuration: ${config.show}")

    val adminSettings = AdminClientSettings[IO]
      .withBootstrapServers(config.exporter.kafka.bootstrapServers)
      .withSecurityConfig(config.exporter.kafka.security)
    val consumerSettings = ConsumerSettings[IO, Array[Byte], Array[Byte]]
      .withBootstrapServers(config.exporter.kafka.bootstrapServers)
      .withGroupId("kafka-exporter")
      .withSecurityConfig(config.exporter.kafka.security)

    adminClientResource(adminSettings).use { adminClient =>
      consumerResource(consumerSettings).use { consumer =>
        val extractor = KafkaExtractor(adminClient, consumer)
        extractor
          .pollMetrics(config.exporter.kafka.groupWhitelist)
          .flatMap(metrics => metrics.traverse_(_.updated[IO]))
          .repeatEvery(config.exporter.pollInterval)
      }.map(_ => ExitCode.Success)
    }
  }

}
