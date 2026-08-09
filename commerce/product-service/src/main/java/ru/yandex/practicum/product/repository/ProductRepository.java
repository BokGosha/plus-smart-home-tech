package ru.yandex.practicum.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.id = :categoryId")
    List<Product> findByCategoryIdWithCategory(Long categoryId);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.name LIKE %:name%")
    List<Product> findByNameWithCategory(String name);

    Optional<Product> findByName(String name);
}
