package com.example.management.application.ports.out;

import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de contrato para cualquier implementación de OrderRepository.
 * El adapter bajo test se instancia en subclases o por configuración.
 */
@DisplayName("OrderRepository contract")
public abstract class OrderRepositoryContractTest {

    protected abstract OrderRepository repository();

    @BeforeEach
    protected void setUp() {
        clearRepository();
    }

    protected void clearRepository() {
        // In-memory implementations can clear; override if needed for other impls
    }

    private static Order newOrder(UUID id, String customerId, List<OrderLine> lines) {
        return new Order(id, customerId, OrderStatus.DRAFT, lines);
    }

    private static OrderLine line(String productId, int qty, String price) {
        return new OrderLine(productId, qty, new BigDecimal(price));
    }

    @Nested
    @DisplayName("save")
    class Save {
        @Test
        void persists_order_and_returns_same_instance() {
            var repo = repository();
            var id = UUID.randomUUID();
            var order = newOrder(id, "customer-1", List.of(line("P1", 2, "10.00")));

            var saved = repo.save(order);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isEqualTo(id);
            assertThat(saved.getCustomerId()).isEqualTo("customer-1");
        }

        @Test
        void findById_returns_saved_order() {
            var repo = repository();
            var id = UUID.randomUUID();
            var order = newOrder(id, "customer-2", List.of(line("P2", 1, "5.50")));

            repo.save(order);
            Optional<Order> found = repo.findById(id);

            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(id);
            assertThat(found.get().getCustomerId()).isEqualTo("customer-2");
            assertThat(found.get().getLines()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        void returns_empty_when_not_found() {
            var repo = repository();
            Optional<Order> found = repo.findById(UUID.randomUUID());
            assertThat(found).isEmpty();
        }
    }
}
