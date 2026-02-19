package com.example.order_management.infrastructure.adapters.in.rest.mapper;

import com.example.order_management.application.ports.in.dto.CreateOrderCommand;
import com.example.order_management.application.ports.out.dto.OrderOutputDto;
import com.example.order_management.infrastructure.adapters.in.rest.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between REST DTOs and application DTOs.
 */
@Component
public class OrderDtoMapper {

    /**
     * Converts CreateOrderRequest to application CreateOrderCommand.
     */
    public CreateOrderCommand toCreateOrderCommand(CreateOrderRequest request) {
        List<CreateOrderCommand.CreateOrderItemCommand> items = request.items().stream()
            .map(item -> new CreateOrderCommand.CreateOrderItemCommand(
                item.productId(),
                item.quantity(),
                item.unitPrice(),
                "USD"
            ))
            .toList();
        return new CreateOrderCommand(request.customerId(), items);
    }

    /**
     * Converts application OrderOutputDto to OrderResponse.
     */
    public OrderResponse toOrderResponse(OrderOutputDto dto) {
        List<OrderResponse.OrderItemResponse> itemResponses = dto.items().stream()
            .map(item -> new OrderResponse.OrderItemResponse(
                item.productId(),
                item.quantity(),
                item.unitPrice()
            ))
            .toList();
        return new OrderResponse(
            dto.orderId(),
            dto.customerId(),
            dto.status(),
            itemResponses,
            dto.totalAmount(),
            dto.currency(),
            dto.createdAt()
        );
    }

    /**
     * Converts application OrderOutputDto to CreateOrderResponse.
     */
    public CreateOrderResponse toCreateOrderResponse(OrderOutputDto dto) {
        return new CreateOrderResponse(
            dto.orderId(),
            dto.status(),
            dto.totalAmount(),
            dto.createdAt()
        );
    }

    /**
     * Converts application OrderOutputDto to PayOrderResponse.
     */
    public PayOrderResponse toPayOrderResponse(OrderOutputDto dto) {
        return new PayOrderResponse(
            dto.orderId(),
            dto.status()
        );
    }
}
