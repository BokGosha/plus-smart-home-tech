package ru.yandex.practicum.collector.service.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.kafka.KafkaClient;
import ru.yandex.practicum.collector.mapping.HubEventMapper;
import ru.yandex.practicum.collector.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    public static final String TOPIC = "telemetry.hubs.v1";

    private final KafkaClient kafkaClient;
    private final HubEventMapper hubEventMapper;

    @Override
    public void collect(HubEvent hubEvent) {
        HubEventAvro hubEventAvro = hubEventMapper.mapHubEvent(hubEvent);

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(TOPIC, hubEvent.getHubId(), hubEventAvro);

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
