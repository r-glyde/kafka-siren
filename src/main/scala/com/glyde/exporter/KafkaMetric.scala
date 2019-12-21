package com.glyde.exporter

import cats.effect.Sync
import com.glyde.exporter.KafkaMetric._
import kamon.Kamon
import kamon.metric.GaugeMetric
import org.apache.kafka.common.TopicPartition

import scala.language.higherKinds

sealed trait KafkaMetric extends Product with Serializable {
  def updated[F[_] : Sync]: F[Unit] = this match {
    case PartitionEnd(p, o) =>
      Sync[F].delay { partitionEnd.refine(Map("topic" -> p.topic, "partition" -> p.partition.toString)).set(o) }
    case GroupMembers(id, members) =>
      Sync[F].delay { groupMembers.refine("consumer_group", id).set(members) }
    case GroupOffset(id, p, o) =>
      Sync[F].delay {
        groupOffset
          .refine(Map("consumer_group" -> id, "topic" -> p.topic, "partition" -> p.partition.toString))
          .set(o)
      }
    case GroupLag(id, p, lag) =>
      Sync[F].delay {
        groupLag
          .refine(Map("consumer_group" -> id, "topic" -> p.topic, "partition" -> p.partition.toString))
          .set(lag)
      }
  }
}

object KafkaMetric {

  final case class PartitionEnd(partition: TopicPartition, offset: Long)            extends KafkaMetric
  final case class GroupMembers(id: String, members: Long)                          extends KafkaMetric
  final case class GroupOffset(id: String, partition: TopicPartition, offset: Long) extends KafkaMetric
  final case class GroupLag(id: String, partition: TopicPartition, lag: Long)       extends KafkaMetric

  val partitionEnd = Kamon.gauge("kafka_topic_partition_end")
  val groupOffset  = Kamon.gauge("kafka_consumer_group_offset")
  val groupLag     = Kamon.gauge("kafka_consumer_group_lag")
  val groupMembers = Kamon.gauge("kafka_consumer_group_members")

}
