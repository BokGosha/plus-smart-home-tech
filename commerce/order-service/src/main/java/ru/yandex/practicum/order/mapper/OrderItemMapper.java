package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.order.dto.OrderItemDto;
import ru.yandex.practicum.order.dto.OrderItemRequest;
import ru.yandex.practicum.order.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    OrderItemDto toOrderItemDto(OrderItem orderItem);

    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(OrderItemRequest request);
}
