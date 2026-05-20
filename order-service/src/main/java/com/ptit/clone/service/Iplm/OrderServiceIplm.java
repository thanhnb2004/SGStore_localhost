package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateOrderRequest;
import com.ptit.clone.dtos.response.OrderItemResponse;
import com.ptit.clone.dtos.response.OrderResponse;
import com.ptit.clone.entity.Order;
import com.ptit.clone.entity.OrderItem;
import com.ptit.clone.messaging.event.CartItemData;
import com.ptit.clone.messaging.event.CartQueryEvent;
import com.ptit.clone.messaging.event.CartQueryResponse;
import com.ptit.clone.messaging.producer.CartQueryProducer;
import com.ptit.clone.model.OrderStatus;
import com.ptit.clone.respository.IOrderRepository;
import com.ptit.clone.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceIplm implements IOrderService {

    private final IOrderRepository orderRepository;
    private final CartQueryProducer cartQueryProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(String userId, CreateOrderRequest request) {
        CartQueryResponse cartData = cartQueryProducer.handle(new CartQueryEvent(userId));

        if (cartData.getItems() == null || cartData.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + userId);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());
        order.setTotalAmount(cartData.getTotalAmount());

        for (CartItemData cartItem : cartData.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(cartItem.getProductId());
            item.setVariantId(cartItem.getVariantId());
            item.setProductName(cartItem.getProductName());
            item.setVariantName(cartItem.getVariantName());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setHeroImage(cartItem.getHeroImage());
            order.getItems().add(item);
        }

        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user: " + userId);
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .variantId(item.getVariantId())
                        .productName(item.getProductName())
                        .variantName(item.getVariantName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice() * item.getQuantity())
                        .heroImage(item.getHeroImage())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
