package acceptance

import base.FeatureSpecBase
import net.manub.embeddedkafka.Codecs.stringSerializer
import net.manub.embeddedkafka.EmbeddedKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.scalatest.concurrent.Eventually
import sttp.client._

import scala.concurrent.duration._

class KafkaSirenFeature extends FeatureSpecBase with Eventually {

  override implicit val patienceConfig = PatienceConfig(15.seconds, 1.second)

  val groupId = "test-consumer-group"
  val topic   = "test-topic"

  implicit val kc = EmbeddedKafkaConfig(
    kafkaPort = 9093,
    zooKeeperPort = 2181,
    customConsumerProperties = Map(GROUP_ID_CONFIG -> groupId)
  )

  feature("Kafka metrics are exported for prometheus") {
    scenario("a consumer group is consuming from a topic") { _ =>
      publishToKafka(topic, (0 to 10).map(x => s"key-$x" -> s"value-$x"))
      consumeNumberStringMessagesFrom(topic, 5)

      eventually {
        val responseBody = basicRequest.get(uri"localhost:9095").send().body.right.value

        List(
          s"""kafka_consumer_group_members{consumer_group="$groupId"}""",
          s"""kafka_consumer_group_lag{consumer_group="$groupId",topic="$topic",partition="0"} 6""",
          s"""kafka_consumer_group_offset{consumer_group="$groupId",topic="$topic",partition="0"} 5""",
          s"""kafka_topic_partition_end{topic="$topic",partition="0"} 11"""
        ).forall(responseBody.contains(_)) shouldBe true
      }
    }

    scenario("a topic is deleted and its metrics are no longer reported") { _ =>
      deleteTopics(List(topic))

      eventually {
        val responseBody = basicRequest.get(uri"localhost:9095").send().body.right.value

        List(
          s"""kafka_consumer_group_lag{consumer_group="$groupId",topic="$topic",partition="0"}""",
          s"""kafka_consumer_group_offset{consumer_group="$groupId",topic="$topic",partition="0"}""",
          s"""kafka_topic_partition_end{topic="$topic",partition="0"}"""
        ).forall(!responseBody.contains(_)) shouldBe true
      }
    }
  }

}
