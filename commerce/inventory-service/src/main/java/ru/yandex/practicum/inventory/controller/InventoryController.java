package ru.yandex.practicum.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.dto.UpdateInventoryRequest;
import ru.yandex.practicum.inventory.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public List<InventoryDto> getInventories() {
        return inventoryService.getInventories();
    }

    @GetMapping("/{productId}")
    public InventoryDto getInventory(@PathVariable Long productId) {
        return inventoryService.getInventory(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryDto createInventory(@Valid @RequestBody ReserveRequest reserveRequest) {
        return inventoryService.createInventory(reserveRequest);
    }

    @PutMapping
    public InventoryDto updateInventory(@Valid @RequestBody UpdateInventoryRequest updateInventoryRequest) {
        return inventoryService.updateInventory(updateInventoryRequest);
    }

    @PostMapping("/reserve")
    public ReserveResponse reserveInventory(@Valid @RequestBody ReserveRequest reserveRequest) {
        return inventoryService.reserveInventory(reserveRequest);
    }

    @PostMapping("/release")
    public ReserveResponse releaseInventory(@Valid @RequestBody ReserveRequest reserveRequest) {
        return inventoryService.releaseInventory(reserveRequest);
    }
}
