package com.ptit.clone.dtos.response;

import com.ptit.clone.model.PaymentMethod;
import com.ptit.clone.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private String userId;
    private Long amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String momoPayUrl;
    private String momoTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
