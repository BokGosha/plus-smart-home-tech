package ru.yandex.practicum.order.entity;

import java.util.List;

public record OrderData(

        String customerName,

        String customerEmail,

        List<OrderItemData> items
) {
}
