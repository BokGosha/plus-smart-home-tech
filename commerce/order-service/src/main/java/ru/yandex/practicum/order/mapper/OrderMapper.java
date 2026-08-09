package ru.yandex.practicum.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.entity.Order;

import java.util.List;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "items", ignore = true)
    Order toOrder(CreateOrderRequest createOrderRequest);

    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);
}
