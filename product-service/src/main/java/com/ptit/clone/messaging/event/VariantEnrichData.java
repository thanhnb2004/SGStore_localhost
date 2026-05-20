package com.ptit.clone.messaging.event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantEnrichData {
    private UUID variantId;
    private UUID productId;
    private String productName;
    private String variantName;
    private String heroImage;
    private Long salePrice;
    private Long listPrice;
}
