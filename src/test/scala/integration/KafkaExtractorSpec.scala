package integration

import base.IntegrationSpecBase
import cats.effect.IO
import com.glyde.exporter.{KafkaExtractor, KafkaMetric}
import com.glyde.exporter.KafkaMetric._
import fs2.kafka._
import net.manub.embeddedkafka.Codecs.stringSerializer
import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.common.TopicPartition

import scala.util.matching.Regex

class KafkaExtractorSpec extends IntegrationSpecBase with EmbeddedKafka {

  "KafkaExtractor" when {
    "pollMetrics" should {

      "return metrics from all consumer groups with empty whitelist" in new TestContext {
        withSetupKafka {
          extractMetrics(List.empty) should contain only
            (metricsForGroup(group1) ++ metricsForGroup(group2) :+ PartitionEnd(partition1, 11): _*)
        }
      }

      "return metrics only for groups matching configured whitelist" in new TestContext {
        withSetupKafka {
          extractMetrics(List("group-1".r)) should contain only (metricsForGroup(group1) :+ PartitionEnd(partition1, 11): _*)
        }
      }

      "return no group metrics if whitelist matches no group" in new TestContext {
        withSetupKafka {
          extractMetrics(List("cupcat".r)) should contain only PartitionEnd(partition1, 11)
        }
      }

    }
  }

  private class TestContext {

    val topic1     = "topic-1"
    val partition1 = new TopicPartition(topic1, 0)
    val group1     = "group-1"
    val group2     = "group-2"

    def withSetupKafka[T](f: => T): T =
      withRunningKafka {
        publishToKafka(topic1, (0 to 10).map(x => s"key-$x" -> s"value-$x"))
        consumeNumberStringMessagesFrom(topic1, 5)(configForGroup(group1))
        consumeNumberStringMessagesFrom(topic1, 5)(configForGroup(group2))
        f
      }

    def metricsForGroup(groupId: String): List[KafkaMetric] = List(
      GroupMembers(groupId, 0),
      GroupOffset(groupId, partition1, 5),
      GroupLag(groupId, partition1, 6)
    )

    def extractMetrics(groupWhitelist: List[Regex]): List[KafkaMetric] = {
      val adminClientSettings = AdminClientSettings[IO].withBootstrapServers(s"localhost:$kafkaPort")
      val consumerSettings = ConsumerSettings[IO, Array[Byte], Array[Byte]]
        .withBootstrapServers(s"localhost:$kafkaPort")
        .withGroupId("test-kafka-exporter")

      adminClientResource(adminClientSettings).use { adminClient =>
        consumerResource(consumerSettings).use { consumer =>
          KafkaExtractor(adminClient, consumer).pollMetrics(groupWhitelist)
        }
      }.unsafeRunSync()
    }

  }

}
