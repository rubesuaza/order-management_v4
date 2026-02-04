package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Línea de pedido: producto, cantidad y precio unitario.
 * Invariantes: quantity > 0, unitPrice >= 0, lineTotal = quantity * unitPrice.
 */
public final class OrderLine {

    private final String productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;

    public OrderLine(String productId, int quantity, BigDecimal unitPrice) {
        if (productId == null || productId.isBlank()) {
            throw new InvalidOrderException("productId cannot be null or blank");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("quantity must be positive");
        }
        if (unitPrice == null) {
            throw new InvalidOrderException("unitPrice cannot be null");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderException("unitPrice cannot be negative");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return quantity == orderLine.quantity
                && Objects.equals(productId, orderLine.productId)
                && Objects.equals(unitPrice, orderLine.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, unitPrice);
    }
}
