package ru.yandex.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.kafka.KafkaClient;
import ru.yandex.practicum.analyzer.service.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaClient kafkaClient;
    private final HubEventHandler hubEventHandler;

    @Value("${kafka.consumer.hub-event.topic}")
    private String topic;

    @Value("${kafka.consume-attempt-timeout-ms:1000}")
    private long consumeAttemptTimeoutMs;

    @Override
    public void run() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getHubEventConsumer();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records =
                        consumer.poll(Duration.ofMillis(consumeAttemptTimeoutMs));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    hubEventHandler.handle((HubEventAvro) record.value());
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            // штатное завершение работы через consumer.wakeup()
        } catch (Exception exception) {
            log.error("Ошибка во время обработки событий хабов", exception);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер событий хабов");
                consumer.close();
            }
        }
    }
}
