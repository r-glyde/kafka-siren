package com.glyde.exporter

import cats.effect.{ExitCode, IO, IOApp, Timer}
import cats.implicits._
import com.typesafe.scalalogging.LazyLogging
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.concurrent.duration.FiniteDuration

object Main extends IOApp with LazyLogging {

  override def run(args: List[String]): IO[ExitCode] = {

    val config = ConfigSource.default.loadOrThrow[Config]
    logger.info(s"Initialised kafka-exporter with configuration: ${config.show}")

    val registeredMetrics =
      List(KafkaMetric.partitionEnd, KafkaMetric.groupMembers, KafkaMetric.groupOffset, KafkaMetric.groupLag)

    val pollMetrics = withAdminClientAndConsumer[Unit, IO](config.exporter) { (adminClient, consumer) =>
      val extractor = KafkaExtractor(adminClient, consumer)
      def repeatWithPrevious(interval: FiniteDuration, previous: List[KafkaMetric]): IO[Unit] =
        extractor
          .pollMetrics(config.exporter.kafka.groupWhitelist)
          .flatMap { metrics =>
            previous.filter(p => metrics.forall(_ =!= p)).traverse(_.remove[IO]) >>
              metrics.traverse(_.update[IO]) >>
              Timer[IO].sleep(interval) >>
              repeatWithPrevious(interval, metrics)
          }
      repeatWithPrevious(config.exporter.pollInterval, List.empty)
    }

    BlazeServerBuilder[IO]
      .bindHttp(config.exporter.port, config.exporter.host)
      .withHttpApp(Router("/" -> HttpRoutes.of[IO] {
        case GET -> Root => Ok(registeredMetrics.map(_.formatOutput).mkString("\n"))
      }).orNotFound)
      .resource
      .use(_ => pollMetrics)
      .as(ExitCode.Success)
  }

}
