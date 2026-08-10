package ru.yandex.practicum.inventory.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.Inventory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryDto toInventoryDto(Inventory inventory);

    @Mapping(target = "reservedQuantity", ignore = true)
    Inventory toInventory(ReserveRequest reserveRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateInventory(UpdateInventoryRequest updateInventoryRequest, @MappingTarget Inventory inventory);

    List<InventoryDto> toInventoryDtoList(List<Inventory> inventory);
}
