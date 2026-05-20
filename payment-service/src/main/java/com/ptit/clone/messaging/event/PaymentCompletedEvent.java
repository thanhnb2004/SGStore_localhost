package com.ptit.clone.messaging.event;

import com.ptit.clone.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private UUID orderId;
    private String userId;
    private Long amount;
    private PaymentMethod method;
}
