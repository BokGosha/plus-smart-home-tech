package ru.yandex.practicum.analyzer.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

public class KafkaClientImpl implements KafkaClient {

    private Consumer<String, SpecificRecordBase> snapshotConsumer;
    private Consumer<String, SpecificRecordBase> hubEventConsumer;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.snapshot.group-id}")
    private String snapshotGroupId;

    @Value("${kafka.consumer.hub-event.group-id}")
    private String hubEventGroupId;

    @Value("${kafka.consumer.snapshot.key-deserializer}")
    private String snapshotKeyDeserializer;

    @Value("${kafka.consumer.snapshot.value-deserializer}")
    private String snapshotValueDeserializer;

    @Value("${kafka.consumer.hub-event.key-deserializer}")
    private String hubEventKeyDeserializer;

    @Value("${kafka.consumer.hub-event.value-deserializer}")
    private String hubEventValueDeserializer;

    @PostConstruct
    public void init() {
        initSnapshotConsumer();
        initHubEventConsumer();
    }

    private void initSnapshotConsumer() {
        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotKeyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        snapshotConsumer = new KafkaConsumer<>(config);
    }

    private void initHubEventConsumer() {
        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubEventGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubEventKeyDeserializer);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubEventValueDeserializer);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        hubEventConsumer = new KafkaConsumer<>(config);
    }

    @Override
    public Consumer<String, SpecificRecordBase> getSnapshotConsumer() {
        if (snapshotConsumer == null) {
            throw new IllegalStateException("Snapshot consumer is not initialized!");
        }

        return snapshotConsumer;
    }

    @Override
    public Consumer<String, SpecificRecordBase> getHubEventConsumer() {
        if (hubEventConsumer == null) {
            throw new IllegalStateException("Hub event consumer is not initialized!");
        }

        return hubEventConsumer;
    }

    @PreDestroy
    public void destroy() {
        if (snapshotConsumer != null) {
            snapshotConsumer.close();
        }

        if (hubEventConsumer != null) {
            hubEventConsumer.close();
        }
    }
}
