package ru.yandex.practicum.collector.model.sensor;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateSensorEvent extends SensorEvent {

    @JsonAlias("temperature_c")
    @NotNull
    private Integer temperatureC;

    @JsonAlias("co2_level")
    @NotNull
    private Integer co2Level;

    @NotNull
    private Integer humidity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
