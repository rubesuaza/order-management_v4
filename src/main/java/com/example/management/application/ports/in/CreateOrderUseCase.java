package com.example.management.application.ports.in;

import com.example.management.domain.model.Order;

/**
 * Puerto de entrada (caso de uso): crear un pedido.
 * El adapter de entrada (controller) invoca este puerto.
 */
public interface CreateOrderUseCase {

    Order create(CreateOrderCommand command);

    record CreateOrderCommand(String customerId, java.util.List<OrderLineDto> lines) {
        public CreateOrderCommand {
            lines = lines != null ? java.util.List.copyOf(lines) : java.util.List.of();
        }
    }

    record OrderLineDto(String productId, int quantity, java.math.BigDecimal unitPrice) {}
}
