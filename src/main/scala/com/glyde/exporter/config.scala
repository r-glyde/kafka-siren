package com.glyde.exporter

import cats.Show
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.common.config.SslConfigs

import scala.concurrent.duration.FiniteDuration
import scala.util.matching.Regex

final case class Config(exporter: ExporterConfig)
final case class ExporterConfig(kafka: KafkaConfig, pollInterval: FiniteDuration)
final case class KafkaConfig(bootstrapServers: String, groupWhitelist: List[Regex], security: SecurityConfig)
final case class SecurityConfig(protocol: String, sslConfig: Option[SslConfig])

final case class SslConfig(keystoreLocation: String,
                           keystorePassword: String,
                           truststoreLocation: String,
                           truststorePassword: String,
                           endpointIdentificationAlgorithm: String) {
  def toMap: Map[String, String] = Map(
    CommonClientConfigs.SECURITY_PROTOCOL_CONFIG            -> "SSL",
    SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG                 -> keystoreLocation,
    SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG                 -> keystorePassword,
    SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG               -> truststoreLocation,
    SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG               -> truststorePassword,
    SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG -> endpointIdentificationAlgorithm
  )
}

object Config {
  implicit val showConfig: Show[Config] = (t: Config) => s"""
       |kafka-brokers: ${t.exporter.kafka.bootstrapServers}
       |poll-interval: ${t.exporter.pollInterval}
       |group-whitelist: ${t.exporter.kafka.groupWhitelist.mkString(",")}
       |security-protocol: ${t.exporter.kafka.security.protocol}
     """.stripMargin
}
