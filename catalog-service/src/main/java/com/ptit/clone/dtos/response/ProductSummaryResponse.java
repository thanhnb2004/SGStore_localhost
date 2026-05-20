package com.ptit.clone.dtos.response;

import com.ptit.clone.entity.AttributeDocument;
import lombok.*;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSummaryResponse {
    // --- Product-level fields ---
    private String productId;
    private String productSlug;
    private String productHeroImage;
    private String variantId;

    private BrandSummaryResponse brand;
    private ProductTypeSummaryResponse productType;
    private ProductStatsSummaryResponse productStats;
    private AttributeSummaryListResponse attributes;
    private VariantSummaryResponse variant;

    //Transient field
    private Float score;

    private Integer discountPercent;    // % giảm giá, tính sẵn ở backend
    private List<String> badgeLabels;   // ["Trả góp 0%", "Quà tặng"]
}
