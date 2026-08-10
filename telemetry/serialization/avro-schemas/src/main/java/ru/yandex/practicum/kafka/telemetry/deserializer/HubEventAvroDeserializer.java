package ru.yandex.practicum.kafka.telemetry.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventAvroDeserializer extends GeneralAvroDeserializer<HubEventAvro> {

    public HubEventAvroDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
