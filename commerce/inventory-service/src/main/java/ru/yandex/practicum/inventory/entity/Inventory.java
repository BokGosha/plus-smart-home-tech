package ru.yandex.practicum.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter @Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;

    @Transient
    public Integer getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    @Version
    private Long version;
}
