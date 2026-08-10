package ru.yandex.practicum.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.entity.Inventory;
import ru.yandex.practicum.inventory.exception.InsufficientStockException;
import ru.yandex.practicum.inventory.exception.NotFoundException;
import ru.yandex.practicum.inventory.mapper.InventoryMapper;
import ru.yandex.practicum.inventory.repository.InventoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public List<InventoryDto> getInventories() {
        return inventoryMapper.toInventoryDtoList(inventoryRepository.findAll());
    }

    public InventoryDto getInventory(Long productId) {
        Inventory inventory = findInventoryByProductId(productId);

        return inventoryMapper.toInventoryDto(inventory);
    }

    @Transactional
    public InventoryDto createInventory(ReserveRequest reserveRequest) {
        if (inventoryRepository.findByProductId(reserveRequest.productId()).isPresent()) {
            throw new IllegalArgumentException("Запись с productId=" + reserveRequest.productId() + " уже существует");
        }

        Inventory inventory = inventoryMapper.toInventory(reserveRequest);

        inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryDto(inventory);
    }

    @Transactional
    public InventoryDto updateInventory(UpdateInventoryRequest updateInventoryRequest) {
        Inventory inventory = findInventoryByProductId(updateInventoryRequest.productId());

        inventoryMapper.updateInventory(updateInventoryRequest, inventory);

        return inventoryMapper.toInventoryDto(inventory);
    }

    @Transactional
    public ReserveResponse reserveInventory(ReserveRequest reserveRequest) {
        Inventory inventory = findInventoryByProductId(reserveRequest.productId());
        Integer quantity = reserveRequest.quantity();

        if (quantity > inventory.getAvailableQuantity()) {
            throw new InsufficientStockException("Недостаточно товара для резервирования");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        return new ReserveResponse(true, inventory.getAvailableQuantity(), "Товар успешно зарезервирован");
    }

    private Inventory findInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Запись с productId=" + productId + " не найдена"));
    }
}
