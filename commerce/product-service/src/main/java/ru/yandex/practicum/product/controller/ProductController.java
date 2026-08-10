package ru.yandex.practicum.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductDto> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/search")
    public List<ProductDto> getProductsByName(@RequestParam String query) {
        return productService.getProductsByName(query);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody CreateProductRequest createProductRequest) {
        return productService.createProduct(createProductRequest);
    }

    @PatchMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest updateProductRequest) {
        return productService.updateProduct(id, updateProductRequest);
    }
}
