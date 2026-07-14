package ru.yandex.practicum.collector.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaClientImpl implements KafkaClient {

    private Producer<String, byte[]> producer;

    @PostConstruct
    public void init() {
        Properties config = new Properties();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");

        producer = new KafkaProducer<>(config);
    }

    @Override
    public Producer<String, byte[]> getProducer() {
        if (producer == null) {
            throw new IllegalStateException("Producer is not initialized!");
        }

        return producer;
    }

    @PreDestroy
    public void destroy() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
