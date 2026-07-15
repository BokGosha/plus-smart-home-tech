package ru.yandex.practicum.aggregator.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.deserializer.SensorAvroDeserializer;
import ru.yandex.practicum.kafka.telemetry.serializer.GeneralAvroSerializer;

import java.util.Properties;

@Component
public class KafkaClientImpl implements KafkaClient {

    private Producer<String, SpecificRecordBase> producer;
    private Consumer<String, SpecificRecordBase> consumer;

    @PostConstruct
    public void init() {
        initProducer();
        initConsumer();
    }

    private void initProducer() {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());

        producer = new KafkaProducer<>(config);
    }

    private void initConsumer() {
        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregator");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorAvroDeserializer.class.getName());

        consumer = new KafkaConsumer<>(config);
    }

    @Override
    public Producer<String, SpecificRecordBase> getProducer() {
        if (producer == null) {
            throw new IllegalStateException("Producer is not initialized!");
        }

        return producer;
    }

    @Override
    public Consumer<String, SpecificRecordBase> getConsumer() {
        if (consumer == null) {
            throw new IllegalStateException("Consumer is not initialized!");
        }

        return consumer;
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }

        if (consumer != null) {
            consumer.close();
        }
    }
}
