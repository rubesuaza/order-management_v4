package com.example.order_management.infrastructure.adapters.in.rest;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.adapters.in.rest.dto.*;
import com.example.order_management.infrastructure.adapters.in.rest.mapper.OrderDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OrderController.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CreateOrderUseCase createOrderUseCase;
    
    @MockBean
    private GetOrderUseCase getOrderUseCase;
    
    @MockBean
    private PayOrderUseCase payOrderUseCase;
    
    @MockBean
    private OrderDtoMapper dtoMapper;
    
    @Test
    void shouldCreateOrder() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        CreateOrderRequest request = new CreateOrderRequest(
            customerId,
            List.of(new CreateOrderRequest.OrderItemRequest(productId, 2, new BigDecimal("15.50")))
        );
        
        Order order = new Order(customerId, List.of(
            new OrderItem(productId, 2, new Money(new BigDecimal("15.50")))
        ));
        
        CreateOrderResponse response = new CreateOrderResponse(
            orderId, "PENDING", new BigDecimal("31.00"), order.getCreatedAt()
        );
        
        when(createOrderUseCase.createOrder(any(), any())).thenReturn(order);
        when(dtoMapper.toCreateOrderResponse(order)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId").exists())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    void shouldGetOrderById() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        Order order = new Order(customerId, List.of(
            new OrderItem(productId, 2, new Money(new BigDecimal("15.50")))
        ));
        
        OrderResponse response = new OrderResponse(
            orderId, customerId, "PENDING",
            List.of(new OrderResponse.OrderItemResponse(productId, 2, new BigDecimal("15.50"))),
            new BigDecimal("31.00"), "USD", order.getCreatedAt()
        );
        
        when(getOrderUseCase.getOrderById(orderId)).thenReturn(Optional.of(order));
        when(dtoMapper.toOrderResponse(order)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId").value(orderId.toString()))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        when(getOrderUseCase.getOrderById(orderId)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldPayOrder() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        
        Order order = new Order(customerId, List.of(
            new OrderItem(UUID.randomUUID(), 2, new Money(new BigDecimal("15.50")))
        ));
        order.pay();
        
        PayOrderResponse response = new PayOrderResponse(orderId, "PAID");
        
        when(payOrderUseCase.payOrder(orderId)).thenReturn(order);
        when(dtoMapper.toPayOrderResponse(order)).thenReturn(response);
        
        // When & Then
        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PAID"));
    }
    
    @Test
    void shouldReturn409WhenOrderCannotBePaid() throws Exception {
        // Given
        UUID orderId = UUID.randomUUID();
        when(payOrderUseCase.payOrder(orderId))
            .thenThrow(new InvalidOrderStateException("Order cannot be paid"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/orders/{orderId}/pay", orderId))
            .andExpect(status().isConflict());
    }
}
