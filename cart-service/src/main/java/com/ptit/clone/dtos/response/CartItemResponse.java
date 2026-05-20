package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CartItemResponse {
    private UUID cartItemId;
    private UUID productId;
    private UUID variantId;
    private Integer quantity;
    private String productName;
    private String variantName;
    private String variantHeroImage;
    private Long salePrice;
    private Long listPrice;
}
