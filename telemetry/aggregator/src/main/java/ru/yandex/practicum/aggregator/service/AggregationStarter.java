package ru.yandex.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.kafka.KafkaClient;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AggregationStarter {

    private static final String SENSORS_TOPIC = "telemetry.sensors.v1";
    private static final String SNAPSHOT_TOPIC = "telemetry.snapshots.v1";
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private final KafkaClient kafkaClient;
    private final SensorsSnapshotService sensorsSnapshotService;

    public void start() {
        Consumer<String, SpecificRecordBase> consumer = kafkaClient.getConsumer();
        Producer<String, SpecificRecordBase> producer = kafkaClient.getProducer();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(SENSORS_TOPIC));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro sensorEventAvro = (SensorEventAvro) record.value();

                    Optional<SensorsSnapshotAvro> updatedSnapshot = sensorsSnapshotService.updateState(sensorEventAvro);
                    updatedSnapshot.ifPresent(snapshot -> {
                        ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                                SNAPSHOT_TOPIC,
                                null,
                                snapshot.getTimestamp().toEpochMilli(),
                                snapshot.getHubId(),
                                snapshot
                        );

                        producer.send(producerRecord);
                    });
                }

                consumer.commitAsync();
            }
        } catch (WakeupException exception) {

        } catch (Exception exception) {
            log.error("Ошибка во время обработки событий от датчиков", exception);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
