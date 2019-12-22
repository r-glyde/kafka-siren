package com.glyde.exporter

import cats.effect.Sync
import cats.implicits._
import com.glyde.exporter.KafkaMetric._
import fs2.kafka.{KafkaAdminClient, KafkaConsumer}
import org.apache.kafka.common.TopicPartition

import scala.collection.JavaConverters._
import scala.language.higherKinds
import scala.util.matching.Regex

final case class KafkaExtractor[F[_] : Sync](adminClient: KafkaAdminClient[F],
                                             consumer: KafkaConsumer[F, Array[Byte], Array[Byte]]) {
  def pollMetrics(groupWhitelist: List[Regex]): F[List[KafkaMetric]] =
    for {
      topics <- adminClient.listTopics.names
      groups <- adminClient.listConsumerGroups.groupIds
      filteredGroups <- Sync[F].pure {
                         if (groupWhitelist.isEmpty) groups
                         else groups.filter(id => groupWhitelist.exists(_.findFirstIn(id).isDefined))
                       }
      partitions    <- topicPartitions(topics.toList)
      endOffsets    <- consumer.endOffsets(partitions.toSet)
      offsetMetrics <- Sync[F].pure(endOffsets.map { case (tp, o) => PartitionEnd(tp, o) }.toList)
      groupMetrics  <- groupMetrics(filteredGroups, endOffsets)
    } yield offsetMetrics ++ groupMetrics

  private def topicPartitions(topics: List[String]): F[List[TopicPartition]] =
    adminClient
      .describeTopics(topics)
      .map(_.flatMap {
        case (topic, desc) => desc.partitions().asScala.map(p => new TopicPartition(topic, p.partition))
      }.toList)

  private def groupMetrics(groups: List[String], endOffsets: Map[TopicPartition, Long]): F[List[KafkaMetric]] =
    adminClient
      .describeConsumerGroups(groups)
      .flatMap(_.toList.flatTraverse {
        case (id, desc) =>
          adminClient
            .listConsumerGroupOffsets(id)
            .partitionsToOffsetAndMetadata
            .map(_.flatMap {
              case (tp, om) =>
                val endOffset = endOffsets.getOrElse(tp, 0L)
                List(GroupOffset(id, tp, om.offset), GroupLag(id, tp, endOffset - om.offset))
            }.toList)
            .map(GroupMembers(id, desc.members.size) +: _)
      })
}
