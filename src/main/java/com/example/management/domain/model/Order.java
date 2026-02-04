package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad raíz del agregado de pedido.
 * Invariantes: id/customerId/status/lines no nulos; lines no vacía; total = suma de lineTotal.
 */
public final class Order {

    private final UUID id;
    private final String customerId;
    private final OrderStatus status;
    private final List<OrderLine> lines;
    private final BigDecimal total;

    public Order(UUID id, String customerId, OrderStatus status, List<OrderLine> lines) {
        if (id == null) {
            throw new InvalidOrderException("order id cannot be null");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidOrderException("customerId cannot be null or blank");
        }
        if (status == null) {
            throw new InvalidOrderException("order status cannot be null");
        }
        if (lines == null || lines.isEmpty()) {
            throw new InvalidOrderException("order must have at least one line");
        }
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.lines = List.copyOf(lines);
        this.total = lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
