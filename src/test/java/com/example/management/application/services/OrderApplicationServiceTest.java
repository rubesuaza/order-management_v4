package com.example.management.application.services;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.exception.InvalidOrderException;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderApplicationService")
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderApplicationService service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("construye Order con líneas y persiste devolviendo el orden guardado")
        void createsOrderAndSavesToRepository() {
            var command = new CreateOrderUseCase.CreateOrderCommand(
                    "customer-1",
                    List.of(
                            new CreateOrderUseCase.OrderLineDto("prod-1", 2, new BigDecimal("10.00")),
                            new CreateOrderUseCase.OrderLineDto("prod-2", 1, new BigDecimal("5.50"))
                    )
            );
            var captor = ArgumentCaptor.forClass(Order.class);
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            Order result = service.create(command);

            verify(orderRepository).save(captor.capture());
            Order saved = captor.getValue();
            assertThat(saved.getCustomerId()).isEqualTo("customer-1");
            assertThat(saved.getStatus()).isEqualTo(OrderStatus.DRAFT);
            assertThat(saved.getLines()).hasSize(2);
            assertThat(saved.getTotal()).isEqualByComparingTo(new BigDecimal("25.50"));
            assertThat(saved.getId()).isNotNull();
            assertThat(result).isSameAs(saved);
        }

        @Test
        @DisplayName("devuelve el orden retornado por el repositorio")
        void returnsOrderFromRepository() {
            var command = new CreateOrderUseCase.CreateOrderCommand(
                    "c1",
                    List.of(new CreateOrderUseCase.OrderLineDto("p1", 1, BigDecimal.ONE))
            );
            var persisted = new Order(
                    UUID.randomUUID(), "c1", OrderStatus.DRAFT,
                    List.of(new OrderLine("p1", 1, BigDecimal.ONE))
            );
            when(orderRepository.save(any(Order.class))).thenReturn(persisted);

            Order result = service.create(command);

            assertThat(result).isSameAs(persisted);
        }

        @Test
        @DisplayName("lanza InvalidOrderException cuando una línea tiene cantidad inválida")
        void throwsWhenLineHasInvalidQuantity() {
            var command = new CreateOrderUseCase.CreateOrderCommand(
                    "c1",
                    List.of(new CreateOrderUseCase.OrderLineDto("p1", 0, BigDecimal.TEN))
            );

            assertThatThrownBy(() -> service.create(command))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessageContaining("quantity");
        }

        @Test
        @DisplayName("lanza InvalidOrderException cuando productId es blanco")
        void throwsWhenProductIdIsBlank() {
            var command = new CreateOrderUseCase.CreateOrderCommand(
                    "c1",
                    List.of(new CreateOrderUseCase.OrderLineDto("  ", 1, BigDecimal.ONE))
            );

            assertThatThrownBy(() -> service.create(command))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessageContaining("productId");
        }

        @Test
        @DisplayName("lanza InvalidOrderException cuando customerId es blanco")
        void throwsWhenCustomerIdIsBlank() {
            var command = new CreateOrderUseCase.CreateOrderCommand(
                    "  ",
                    List.of(new CreateOrderUseCase.OrderLineDto("p1", 1, BigDecimal.ONE))
            );

            assertThatThrownBy(() -> service.create(command))
                    .isInstanceOf(InvalidOrderException.class)
                    .hasMessageContaining("customerId");
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("delega en repository y devuelve Optional presente cuando existe")
        void returnsOrderWhenFound() {
            var id = UUID.randomUUID();
            var order = new Order(
                    id, "c1", OrderStatus.DRAFT,
                    List.of(new OrderLine("p1", 1, BigDecimal.ONE))
            );
            when(orderRepository.findById(id)).thenReturn(Optional.of(order));

            Optional<Order> result = service.getById(id);

            assertThat(result).isPresent();
            assertThat(result.get()).isSameAs(order);
            verify(orderRepository).findById(id);
        }

        @Test
        @DisplayName("delega en repository y devuelve Optional vacío cuando no existe")
        void returnsEmptyWhenNotFound() {
            var id = UUID.randomUUID();
            when(orderRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Order> result = service.getById(id);

            assertThat(result).isEmpty();
            verify(orderRepository).findById(id);
        }
    }
}
