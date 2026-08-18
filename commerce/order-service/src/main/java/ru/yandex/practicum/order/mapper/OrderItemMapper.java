package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.order.dto.OrderItemDto;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.dto.OrderItemData;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemDto toOrderItemDto(OrderItem orderItem);

    OrderItem toOrderItem(OrderItemData orderData);
}
