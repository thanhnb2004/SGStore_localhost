package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemData {
    private UUID productId;
    private UUID variantId;
    private String productName;
    private String variantName;
    private Integer quantity;
    private Long unitPrice;
    private String heroImage;
}
