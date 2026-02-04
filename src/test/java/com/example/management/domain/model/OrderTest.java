package com.example.management.domain.model;

import com.example.management.domain.exception.InvalidOrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order")
class OrderTest {

    private static OrderLine line(String productId, int qty, String price) {
        return new OrderLine(productId, qty, new BigDecimal(price));
    }

    @Nested
    @DisplayName("invariantes")
    class Invariants {

        @Test
        @DisplayName("crea pedido válido con líneas y total coherente")
        void createsValidOrder() {
            List<OrderLine> lines = List.of(
                    line("p1", 2, "10.00"),
                    line("p2", 1, "5.50")
            );
            Order order = new Order(UUID.randomUUID(), "customer-1", OrderStatus.DRAFT, lines);
            assertEquals(new BigDecimal("25.50"), order.getTotal());
            assertEquals(2, order.getLines().size());
        }

        @Test
        @DisplayName("total es suma de lineTotal de las líneas")
        void totalIsSumOfLineTotals() {
            List<OrderLine> lines = List.of(
                    line("a", 3, "4.00"),
                    line("b", 2, "3.50")
            );
            Order order = new Order(UUID.randomUUID(), "c1", OrderStatus.CONFIRMED, lines);
            assertEquals(new BigDecimal("19.00"), order.getTotal());
        }

        @Test
        @DisplayName("rechaza lista de líneas vacía")
        void rejectsEmptyLines() {
            assertThrows(InvalidOrderException.class,
                    () -> new Order(UUID.randomUUID(), "c1", OrderStatus.DRAFT, List.of()));
        }

        @Test
        @DisplayName("rechaza líneas nulas")
        void rejectsNullLines() {
            assertThrows(InvalidOrderException.class,
                    () -> new Order(UUID.randomUUID(), "c1", OrderStatus.DRAFT, null));
        }

        @Test
        @DisplayName("rechaza customerId nulo o vacío")
        void rejectsBlankCustomerId() {
            List<OrderLine> lines = List.of(line("p1", 1, "1.00"));
            assertThrows(InvalidOrderException.class,
                    () -> new Order(UUID.randomUUID(), null, OrderStatus.DRAFT, lines));
            assertThrows(InvalidOrderException.class,
                    () -> new Order(UUID.randomUUID(), "  ", OrderStatus.DRAFT, lines));
        }

        @Test
        @DisplayName("rechaza status nulo")
        void rejectsNullStatus() {
            List<OrderLine> lines = List.of(line("p1", 1, "1.00"));
            assertThrows(InvalidOrderException.class,
                    () -> new Order(UUID.randomUUID(), "c1", null, lines));
        }

        @Test
        @DisplayName("rechaza id nulo")
        void rejectsNullId() {
            List<OrderLine> lines = List.of(line("p1", 1, "1.00"));
            assertThrows(InvalidOrderException.class,
                    () -> new Order(null, "c1", OrderStatus.DRAFT, lines));
        }

        @Test
        @DisplayName("las líneas devueltas son inmutables (copia defensiva)")
        void linesAreDefensivelyCopied() {
            List<OrderLine> lines = List.of(line("p1", 1, "1.00"));
            Order order = new Order(UUID.randomUUID(), "c1", OrderStatus.DRAFT, lines);
            assertThrows(UnsupportedOperationException.class, () -> order.getLines().add(line("p2", 1, "1.00")));
        }
    }

    @Nested
    @DisplayName("estados")
    class Status {

        @Test
        @DisplayName("acepta todos los estados válidos")
        void acceptsAllValidStatuses() {
            List<OrderLine> lines = List.of(line("p1", 1, "1.00"));
            for (OrderStatus status : OrderStatus.values()) {
                Order order = new Order(UUID.randomUUID(), "c1", status, lines);
                assertEquals(status, order.getStatus());
            }
        }
    }
}
