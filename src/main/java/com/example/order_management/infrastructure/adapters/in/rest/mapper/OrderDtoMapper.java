package com.example.order_management.infrastructure.adapters.in.rest.mapper;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.infrastructure.adapters.in.rest.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between DTOs and Domain models.
 */
@Component
public class OrderDtoMapper {
    
    /**
     * Converts CreateOrderRequest to CreateOrderUseCase.OrderItemRequest list.
     */
    public List<CreateOrderUseCase.OrderItemRequest> toOrderItemRequests(
            List<CreateOrderRequest.OrderItemRequest> items) {
        return items.stream()
            .map(item -> new CreateOrderUseCase.OrderItemRequest(
                item.productId(),
                item.quantity(),
                item.unitPrice(),
                "USD" // Default currency, can be extracted from request if needed
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Converts Domain Order to OrderResponse.
     */
    public OrderResponse toOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
            .map(item -> new OrderResponse.OrderItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().getAmount()
            ))
            .collect(Collectors.toList());
        
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getStatus().name(),
            itemResponses,
            order.getTotalAmount().getAmount(),
            order.getTotalAmount().getCurrency(),
            order.getCreatedAt()
        );
    }
    
    /**
     * Converts Domain Order to CreateOrderResponse.
     */
    public CreateOrderResponse toCreateOrderResponse(Order order) {
        return new CreateOrderResponse(
            order.getId(),
            order.getStatus().name(),
            order.getTotalAmount().getAmount(),
            order.getCreatedAt()
        );
    }
    
    /**
     * Converts Domain Order to PayOrderResponse.
     */
    public PayOrderResponse toPayOrderResponse(Order order) {
        return new PayOrderResponse(
            order.getId(),
            order.getStatus().name()
        );
    }
}
