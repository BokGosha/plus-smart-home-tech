package ru.yandex.practicum.kafka.telemetry.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorsSnapshotAvroDeserializer extends GeneralAvroDeserializer<SensorsSnapshotAvro> {

    public SensorsSnapshotAvroDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
