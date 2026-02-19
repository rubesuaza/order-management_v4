package com.example.order_management.infrastructure.persistence.repository;

import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for OrderEntity.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    /**
     * Fetches order with items in a single query to avoid N+1 / lazy-load.
     */
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderEntity> findByIdWithItems(@Param("id") UUID id);
}
