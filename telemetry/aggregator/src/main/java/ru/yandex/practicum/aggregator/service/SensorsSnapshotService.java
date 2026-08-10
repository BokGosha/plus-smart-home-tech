package ru.yandex.practicum.aggregator.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;

public interface SensorsSnapshotService {

    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event);
}
