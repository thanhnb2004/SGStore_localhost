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
public class VariantEnrichData {
    private UUID variantId;
    private UUID productId;
    private String productName;
    private String variantName;
    private String heroImage;
    private Long salePrice;
    private Long listPrice;
}
