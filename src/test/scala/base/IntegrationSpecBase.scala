package base

import net.manub.embeddedkafka.EmbeddedKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.scalatest.{Matchers, WordSpec}

abstract class IntegrationSpecBase extends WordSpec with Matchers with IOSpecBase {

  val (kafkaPort, zookeeperPort) = (9092, 2181)

  implicit lazy val kafkaConfig: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort, zookeeperPort)

  def configForGroup(groupId: String): EmbeddedKafkaConfig = EmbeddedKafkaConfig(
    kafkaPort = kafkaPort,
    zooKeeperPort = zookeeperPort,
    customConsumerProperties = Map(GROUP_ID_CONFIG -> groupId)
  )

}
