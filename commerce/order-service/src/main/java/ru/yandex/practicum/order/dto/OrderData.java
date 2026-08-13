package ru.yandex.practicum.order.dto;

import java.util.List;

public record OrderData(

        String customerName,

        String customerEmail,

        String status,

        String statusDetails,

        List<OrderItemData> items
) {
}
