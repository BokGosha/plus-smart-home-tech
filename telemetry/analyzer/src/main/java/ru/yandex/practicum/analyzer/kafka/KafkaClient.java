package ru.yandex.practicum.analyzer.kafka;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;

public interface KafkaClient {

    Consumer<String, SpecificRecordBase> getSnapshotConsumer();
    Consumer<String, SpecificRecordBase> getHubEventConsumer();
}
