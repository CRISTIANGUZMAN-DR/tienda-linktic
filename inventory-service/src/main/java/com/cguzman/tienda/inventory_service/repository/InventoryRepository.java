package com.cguzman.tienda.inventory_service.repository;

import com.cguzman.tienda.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    // PESSIMISTIC_WRITE: bloquea la fila mientras dura la transacción
    // Así dos compras simultáneas no pueden leer el mismo stock al mismo tiempo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdForUpdate(@Param("productId") UUID productId);

    Optional<Inventory> findByProductId(UUID productId);
}