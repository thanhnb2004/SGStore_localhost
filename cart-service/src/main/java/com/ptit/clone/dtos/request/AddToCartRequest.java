package com.ptit.clone.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddToCartRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private UUID variantId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
