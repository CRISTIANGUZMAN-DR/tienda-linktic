package com.cguzman.tienda.product_service.repository;

import com.cguzman.tienda.product_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    Optional<Product> findBySku(String sku);

    // Filtro por status + búsqueda por sku o name
    @Query("""
    SELECT p FROM Product p
    WHERE (:status IS NULL OR p.status = :status)
    AND (:search IS NULL OR
         LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR
         LOWER(p.sku)  LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
    """)
    Page<Product> findWithFilters(
            @Param("status") Product.Status status,
            @Param("search") String search,
            Pageable pageable
    );
}