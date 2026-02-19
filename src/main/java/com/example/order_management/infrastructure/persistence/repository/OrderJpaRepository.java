package com.example.order_management.infrastructure.persistence.repository;

import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA Repository for OrderEntity.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    // Spring Data JPA provides basic CRUD operations
}
