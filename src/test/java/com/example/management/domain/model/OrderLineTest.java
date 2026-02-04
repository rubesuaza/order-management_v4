package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderLine")
class OrderLineTest {

    @Nested
    @DisplayName("invariantes")
    class Invariants {

        @Test
        @DisplayName("crea línea válida con cantidad positiva y precio no negativo")
        void createsValidLine() {
            OrderLine line = new OrderLine("prod-1", 2, new BigDecimal("10.50"));
            assertEquals("prod-1", line.getProductId());
            assertEquals(2, line.getQuantity());
            assertEquals(new BigDecimal("10.50"), line.getUnitPrice());
            assertEquals(new BigDecimal("21.00"), line.getLineTotal());
        }

        @Test
        @DisplayName("lineTotal es quantity * unitPrice")
        void lineTotalIsQuantityTimesUnitPrice() {
            OrderLine line = new OrderLine("p1", 3, new BigDecimal("4.99"));
            assertEquals(new BigDecimal("14.97"), line.getLineTotal());
        }

        @Test
        @DisplayName("rechaza cantidad cero")
        void rejectsZeroQuantity() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine("p1", 0, new BigDecimal("10.00")));
        }

        @Test
        @DisplayName("rechaza cantidad negativa")
        void rejectsNegativeQuantity() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine("p1", -1, new BigDecimal("10.00")));
        }

        @Test
        @DisplayName("rechaza precio unitario negativo")
        void rejectsNegativeUnitPrice() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine("p1", 1, new BigDecimal("-0.01")));
        }

        @Test
        @DisplayName("acepta precio cero")
        void acceptsZeroUnitPrice() {
            OrderLine line = new OrderLine("p1", 1, BigDecimal.ZERO);
            assertEquals(BigDecimal.ZERO, line.getLineTotal());
        }

        @Test
        @DisplayName("rechaza productId nulo")
        void rejectsNullProductId() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine(null, 1, BigDecimal.ONE));
        }

        @Test
        @DisplayName("rechaza productId vacío")
        void rejectsBlankProductId() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine("  ", 1, BigDecimal.ONE));
        }

        @Test
        @DisplayName("rechaza unitPrice nulo")
        void rejectsNullUnitPrice() {
            assertThrows(InvalidOrderException.class,
                    () -> new OrderLine("p1", 1, null));
        }
    }
}
