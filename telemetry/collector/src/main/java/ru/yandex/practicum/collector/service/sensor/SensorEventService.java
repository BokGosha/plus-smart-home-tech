package ru.yandex.practicum.collector.service.sensor;

import ru.yandex.practicum.collector.model.sensor.SensorEvent;

public interface SensorEventService {

    void collect(SensorEvent sensorEvent);
}
