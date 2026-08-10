package ru.yandex.practicum.order.entity;

import java.math.BigDecimal;

public record OrderItemData(

        Long productId,

        String productName,

        BigDecimal price,

        Integer quantity
) {
}
