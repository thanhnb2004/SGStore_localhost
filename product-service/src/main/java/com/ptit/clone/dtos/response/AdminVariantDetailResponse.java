package com.ptit.clone.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVariantDetailResponse {
    private UUID variantId;
    private String sku;
    private String barcode;
    private String name;
    private String status;
    private Long listPrice;
    private Long salePrice;
    private String availabilityStatus;
    private String heroImage;
    private List<String> images;
    private Map<String, String> optionValues;
}
