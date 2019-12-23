package unit

import base.PropertyCheckSpecBase
import cats.syntax.eq._
import com.glyde.exporter.KafkaMetric
import com.glyde.exporter.KafkaMetric._
import org.apache.kafka.common.TopicPartition

import utils.ArbitraryInstances._

class KafkaMetricSpec extends PropertyCheckSpecBase {

  "eq" should {

    "return equal for the same metric" in {
      forAll { metric: KafkaMetric =>
        metric eqv metric shouldBe true
      }
    }

    "return equal for class with the same tags but different measurement" in {
      forAll { (groupId: String, tp: TopicPartition) =>
        GroupOffset(groupId, tp, 0).asInstanceOf[KafkaMetric] eqv GroupOffset(groupId, tp, 1) shouldBe true
      }
    }

    "return not equal for different classes with the same tags" in {
      forAll { (groupId: String, tp: TopicPartition) =>
        GroupOffset(groupId, tp, 0).asInstanceOf[KafkaMetric] eqv GroupLag(groupId, tp, 0) shouldBe false
      }
    }

  }

}
