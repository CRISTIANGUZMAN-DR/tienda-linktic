package com.cguzman.tienda.inventory_service.repository;

import com.cguzman.tienda.inventory_service.model.PurchaseIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseIdempotencyRepository extends JpaRepository<PurchaseIdempotency, String> {
}