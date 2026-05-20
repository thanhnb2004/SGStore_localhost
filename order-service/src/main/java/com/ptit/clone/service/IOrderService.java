package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateOrderRequest;
import com.ptit.clone.dtos.response.OrderResponse;
import com.ptit.clone.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface IOrderService {
    OrderResponse createOrder(String userId, CreateOrderRequest request);
    OrderResponse getOrder(UUID orderId);
    List<OrderResponse> getOrdersByUser(String userId);
    OrderResponse updateStatus(UUID orderId, OrderStatus status);
    OrderResponse cancelOrder(UUID orderId, String userId);
}
