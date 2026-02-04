package com.example.management.application.ports.out;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de pedidos.
 * El adapter (infraestructura) implementa esta interfaz.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);
}
