package utils

import com.danielasfregola.randomdatagenerator.RandomDataGenerator
import com.glyde.exporter.KafkaMetric
import com.glyde.exporter.KafkaMetric._
import org.apache.kafka.common.TopicPartition
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryInstances extends RandomDataGenerator {

  implicit val kafkaMetricArb: Arbitrary[KafkaMetric] = Arbitrary {
    Gen.oneOf(
      arbitrary[PartitionEnd],
      arbitrary[GroupOffset],
      arbitrary[GroupLag],
      arbitrary[GroupMembers]
    )
  }

  implicit val topicPartitionArb: Arbitrary[TopicPartition] = Arbitrary {
    for {
      topic     <- arbitrary[String]
      partition <- arbitrary[Int]
    } yield new TopicPartition(topic, partition)
  }

}
