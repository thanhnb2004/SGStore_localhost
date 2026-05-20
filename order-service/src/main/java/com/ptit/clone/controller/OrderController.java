package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.CreateOrderRequest;
import com.ptit.clone.dtos.response.OrderResponse;
import com.ptit.clone.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrder(orderId));
    }

    @GetMapping("/")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrdersByUser(userId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.cancelOrder(orderId, userId));
    }
}
