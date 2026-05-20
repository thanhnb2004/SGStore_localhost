package com.ptit.clone.dtos.request;

import com.ptit.clone.model.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    @Min(1)
    private Long amount;

    @NotNull
    private PaymentMethod method;
}
