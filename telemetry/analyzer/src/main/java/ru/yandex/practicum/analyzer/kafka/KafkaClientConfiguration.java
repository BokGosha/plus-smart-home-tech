package ru.yandex.practicum.analyzer.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaClientConfiguration {

    @Bean
    public KafkaClient kafkaClient() {
        return new KafkaClientImpl();
    }
}
