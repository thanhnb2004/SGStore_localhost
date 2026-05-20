package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class VariantSummaryResponse {
    private String sku;
    private String variantName;
    private Long listPrice;
    private Long salePrice;
    private String variantHeroImage;
    private String availabilityStatus;
}
//re
