package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de entrada: obtener un pedido por id.
 */
public interface GetOrderUseCase {

    Optional<Order> getById(UUID id);
}
