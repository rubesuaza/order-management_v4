package com.example.order_management.infrastructure.adapters.in.rest;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.domain.exception.InvalidOrderDataException;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.exception.OrderNotFoundException;
import com.example.order_management.infrastructure.adapters.in.rest.dto.*;
import com.example.order_management.infrastructure.adapters.in.rest.mapper.OrderDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Order management endpoints.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final OrderDtoMapper dtoMapper;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            PayOrderUseCase payOrderUseCase,
            OrderDtoMapper dtoMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Creates a new order.
     * POST /api/v1/orders
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        var command = dtoMapper.toCreateOrderCommand(request);
        var orderOutput = createOrderUseCase.createOrder(command);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(dtoMapper.toCreateOrderResponse(orderOutput));
    }

    /**
     * Gets order details by ID.
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getOrderById(orderId)
            .map(dto -> ResponseEntity.ok(dtoMapper.toOrderResponse(dto)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Pays an order.
     * POST /api/v1/orders/{orderId}/pay
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PayOrderResponse> payOrder(@PathVariable UUID orderId) {
        var orderOutput = payOrderUseCase.payOrder(orderId);
        return ResponseEntity.ok(dtoMapper.toPayOrderResponse(orderOutput));
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<String> handleInvalidOrderState(InvalidOrderStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFound(OrderNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidOrderDataException.class)
    public ResponseEntity<String> handleInvalidOrderData(InvalidOrderDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
