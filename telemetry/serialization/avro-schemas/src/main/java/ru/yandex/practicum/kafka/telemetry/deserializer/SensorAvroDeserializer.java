package ru.yandex.practicum.kafka.telemetry.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorAvroDeserializer extends GeneralAvroDeserializer<SensorEventAvro> {
    public SensorAvroDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }
}
