package com.glyde.exporter

import cats.Eq
import cats.effect.Sync
import com.glyde.exporter.KafkaMetric._
import org.apache.kafka.common.TopicPartition

import scala.language.higherKinds

sealed trait KafkaMetric extends Product with Serializable {
  def gauge: prometheus.Gauge = this match {
    case _: PartitionEnd => partitionEnd
    case _: GroupMembers => groupMembers
    case _: GroupOffset  => groupOffset
    case _: GroupLag     => groupLag
  }

  def measurement: Double = this match {
    case PartitionEnd(_, offset)   => offset
    case GroupMembers(_, members)  => members
    case GroupOffset(_, _, offset) => offset
    case GroupLag(_, _, lag)       => lag
  }

  def tags: Map[String, String] = this match {
    case PartitionEnd(p, _)    => Map("topic"          -> p.topic, "partition" -> p.partition.toString)
    case GroupMembers(id, _)   => Map("consumer_group" -> id)
    case GroupOffset(id, p, _) => Map("consumer_group" -> id, "topic" -> p.topic, "partition" -> p.partition.toString)
    case GroupLag(id, p, _)    => Map("consumer_group" -> id, "topic" -> p.topic, "partition" -> p.partition.toString)
  }

  def update[F[_] : Sync]: F[Unit] = Sync[F].delay(gauge.update(tags, measurement))

  def remove[F[_] : Sync]: F[Unit] = Sync[F].delay(gauge.remove(tags))
}

object KafkaMetric {

  final case class PartitionEnd(partition: TopicPartition, offset: Long)            extends KafkaMetric
  final case class GroupMembers(id: String, members: Long)                          extends KafkaMetric
  final case class GroupOffset(id: String, partition: TopicPartition, offset: Long) extends KafkaMetric
  final case class GroupLag(id: String, partition: TopicPartition, lag: Long)       extends KafkaMetric

  val partitionEnd = prometheus.Gauge("kafka_topic_partition_end")
  val groupOffset  = prometheus.Gauge("kafka_consumer_group_offset")
  val groupLag     = prometheus.Gauge("kafka_consumer_group_lag")
  val groupMembers = prometheus.Gauge("kafka_consumer_group_members")

  implicit val metricEquality: Eq[KafkaMetric] = (a, b) =>
    (a.tags == b.tags) && (a.getClass.getSimpleName == b.getClass.getSimpleName)

}
