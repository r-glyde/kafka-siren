# Kafka Siren

Export metrics from a kafka cluster for prometheus

### Configuration
Configuration options can be passed via environment variables, system properties or a configuration file.

| Property                           | Environment Variable           | Default    | Notes |
| -------------                      |-------------                   | -----      | ----- |
| `exporter.kafka.bootstrap-servers` | `KAFKA_BROKERS`                | -          | Address of kafka servers (`host:port,host:port...`)                                       |
| `exporter.kafka.group-whitelist`   | `KAFKA_GROUP_WHITELIST`        | []         | List of regex to filter which consumer groups are reported (empty results in no filtering) |
| `exporter.poll-interval`           | `KAFKA_EXPORTER_POLL_INTERVAL` | 30 seconds | How often to collect metrics                                                              |
| `exporter.port`                    | `KAFKA_EXPORTER_PORT`          | 9095       | Which port to make metrics available on                                                   |

### Run
Equivalent ways of running `kafka-siren` to collect metrics for
consumer groups matching the regex `group-1` from a kafka server
running at `localhost:9093` and expose them on port `9000`.

```bash
docker run -p 9000:9000 \
    --env KAFKA_BROKERS=localhost:9093 \
    --env KAFKA_GROUP_WHITELIST.0=group-1 \
    --env KAFKA_EXPORTER_PORT=9000 \
    glyderj/kafka-siren
```

```bash
docker run -p 9000:9000 \
    --env JAVA_OPTS="-Dexporter.kafka.bootstrap-servers=localhost:9093 -Dexporter.kafka.group-whitelist.0=group-1 -Dexporter.port=9000" \
    glyderj/kafka-siren
```

Mount the below `application.conf` inside your container.
```hocon
exporter {
  kafka {
    bootstrap-servers = "localhost:9093"
    group-whitelist = ["topic-1"]
  }
  port = 9000
}
```
Then:
```bash
docker run -p 9000:9000 \
    -v $(pwd):/opt/docker/conf/ \
    --env JAVA_OPTS="-Dconfig.file=/opt/docker/conf/application.conf" \
    glyderj/kafka-siren
```

### Metrics

#### Topics
__`kafka_topic_partition_end`__

#### Consumer Groups
__`kafka_consumer_group_members`__

__`kafka_consumer_group_offset`__

__`kafka_consumer_group_lag`__

#### Example
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
