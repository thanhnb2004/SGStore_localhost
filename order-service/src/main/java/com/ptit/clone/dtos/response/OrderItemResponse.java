package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderItemResponse {
    private UUID id;
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String variantName;
    private Integer quantity;
    private Long unitPrice;
    private Long subtotal;
    private String heroImage;
}
