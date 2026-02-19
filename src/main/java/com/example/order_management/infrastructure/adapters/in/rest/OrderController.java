package com.example.order_management.infrastructure.adapters.in.rest;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.domain.exception.InvalidOrderStateException;
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
        
        if (request.items() == null || request.items().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        var orderItemRequests = dtoMapper.toOrderItemRequests(request.items());
        var order = createOrderUseCase.createOrder(request.customerId(), orderItemRequests);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(dtoMapper.toCreateOrderResponse(order));
    }
    
    /**
     * Gets order details by ID.
     * GET /api/v1/orders/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return getOrderUseCase.getOrderById(orderId)
            .map(order -> ResponseEntity.ok(dtoMapper.toOrderResponse(order)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Pays an order.
     * POST /api/v1/orders/{orderId}/pay
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PayOrderResponse> payOrder(@PathVariable UUID orderId) {
        try {
            var order = payOrderUseCase.payOrder(orderId);
            return ResponseEntity.ok(dtoMapper.toPayOrderResponse(order));
        } catch (InvalidOrderStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Global exception handler for domain exceptions.
     */
    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<String> handleInvalidOrderState(InvalidOrderStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
