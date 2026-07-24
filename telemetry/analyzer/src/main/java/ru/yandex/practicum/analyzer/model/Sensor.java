package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@ToString
public class Sensor {

    @Id
    private String id;

    private String hubId;
}
