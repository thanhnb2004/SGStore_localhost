package com.ptit.clone.dtos.response;

import com.ptit.clone.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderResponse {
    private UUID id;
    private String userId;
    private Long totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
