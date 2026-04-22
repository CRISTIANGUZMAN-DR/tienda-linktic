package com.cguzman.tienda.product_service.mapper;

import com.cguzman.tienda.product_service.dto.ProductRequest;
import com.cguzman.tienda.product_service.dto.ProductResponse;
import com.cguzman.tienda.product_service.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest req) {
        return Product.builder()
                .sku(req.getSku())
                .name(req.getName())
                .price(req.getPrice())
                .status(Product.Status.valueOf(req.getStatus()))
                .build();
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .price(p.getPrice())
                .status(p.getStatus().name())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}