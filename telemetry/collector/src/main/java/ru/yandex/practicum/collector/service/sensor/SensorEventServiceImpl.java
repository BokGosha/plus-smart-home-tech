package ru.yandex.practicum.collector.service.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.kafka.KafkaClient;
import ru.yandex.practicum.collector.mapping.SensorEventMapper;
import ru.yandex.practicum.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class SensorEventServiceImpl implements SensorEventService {

    public static final String TOPIC = "telemetry.sensors.v1";

    private final KafkaClient kafkaClient;
    private final SensorEventMapper sensorEventMapper;

    @Override
    public void collect(SensorEvent sensorEvent) {
        SensorEventProto sensorEventProto = sensorEventMapper.mapSensorEvent(sensorEvent);

        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                TOPIC,
                null,
                sensorEvent.getTimestamp().toEpochMilli(),
                sensorEvent.getHubId(),
                sensorEventProto.toByteArray()
        );

        try {
            kafkaClient.getProducer().send(record).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka send interrupted", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Kafka send failed", e);
        }
    }
}
