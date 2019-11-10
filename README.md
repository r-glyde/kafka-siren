# Kafka Siren

Export metrics from a kafka cluster for prometheus

> "... inadequate, even childish measures, may serve to rescue one from peril."
>
> &mdash; Franz Kafka (introduction to 'The Silence of the Sirens')

### Run
##### Options

### Metrics

##### Topics
__`kafka_topic_partition_end`__

##### Consumer Groups
__`kafka_consumer_group_members`__

__`kafka_consumer_group_offset`__

__`kafka_consumer_group_lag`__

##### Example
```
# TYPE kafka_consumer_group_members gauge
kafka_consumer_group_members{consumer_group="group-1"} 0.0
# TYPE kafka_consumer_group_lag gauge
kafka_consumer_group_lag{consumer_group="group-1",topic="topic-1",partition="0"} 1972.0
kafka_consumer_group_lag{consumer_group="group-1",topic="topic-1",partition="1"} 1913.0
kafka_consumer_group_lag{consumer_group="group-1",topic="topic-1",partition="2"} 2065.0
kafka_consumer_group_lag{consumer_group="group-1",topic="topic-1",partition="3"} 2099.0
kafka_consumer_group_lag{consumer_group="group-1",topic="topic-1",partition="4"} 1951.0
# TYPE kafka_consumer_group_offset gauge
kafka_consumer_group_offset{consumer_group="group-1",topic="topic-1",partition="0"} 4006.0
kafka_consumer_group_offset{consumer_group="group-1",topic="topic-1",partition="1"} 3984.0
kafka_consumer_group_offset{consumer_group="group-1",topic="topic-1",partition="2"} 3985.0
kafka_consumer_group_offset{consumer_group="group-1",topic="topic-1",partition="3"} 4010.0
kafka_consumer_group_offset{consumer_group="group-1",topic="topic-1",partition="4"} 4015.0
# TYPE kafka_topic_partition_end gauge
kafka_topic_partition_end{topic="topic-1",partition="0"} 5978.0
kafka_topic_partition_end{topic="topic-1",partition="1"} 5897.0
kafka_topic_partition_end{topic="topic-1",partition="2"} 6050.0
kafka_topic_partition_end{topic="topic-1",partition="3"} 6109.0
kafka_topic_partition_end{topic="topic-1",partition="4"} 5966.0
```