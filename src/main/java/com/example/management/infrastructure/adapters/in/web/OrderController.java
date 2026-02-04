package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.domain.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de entrada: REST API para gestión de pedidos.
 * Depende únicamente de los puertos de entrada (casos de uso).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, GetOrderUseCase getOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest request) {
        List<CreateOrderUseCase.OrderLineDto> lineDtos = Optional.ofNullable(request.lines())
                .orElseGet(List::of)
                .stream()
                .map(l -> new CreateOrderUseCase.OrderLineDto(l.productId(), l.quantity(), l.unitPrice()))
                .toList();
        var command = new CreateOrderUseCase.CreateOrderCommand(request.customerId(), lineDtos);
        Order order = createOrderUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        return getOrderUseCase.getById(id)
                .map(OrderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CreateOrderRequest(String customerId, List<OrderLineRequest> lines) {}
    public record OrderLineRequest(String productId, int quantity, BigDecimal unitPrice) {}

    public record OrderResponse(
            UUID id,
            String customerId,
            String status,
            List<OrderLineResponse> lines,
            BigDecimal total
    ) {
        static OrderResponse from(Order order) {
            var lineResponses = order.getLines().stream()
                    .map(l -> new OrderLineResponse(l.getProductId(), l.getQuantity(), l.getUnitPrice(), l.getLineTotal()))
                    .toList();
            return new OrderResponse(
                    order.getId(),
                    order.getCustomerId(),
                    order.getStatus().name(),
                    lineResponses,
                    order.getTotal()
            );
        }
    }
    public record OrderLineResponse(String productId, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
}
