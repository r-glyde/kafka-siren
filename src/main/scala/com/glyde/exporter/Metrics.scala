package com.glyde.exporter

import kamon.Kamon

object Metrics {

  val partitionEnd = Kamon.gauge("kafka_topic_partition_end")
  val groupOffset  = Kamon.gauge("kafka_consumer_group_offset")
  val groupLag     = Kamon.gauge("kafka_consumer_group_lag")
  val groupMembers = Kamon.gauge("kafka_consumer_group_members")

}
