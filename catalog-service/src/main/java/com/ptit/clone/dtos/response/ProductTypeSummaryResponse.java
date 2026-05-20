package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductTypeSummaryResponse {
    private String productTypeSlug;
    private String productTypeName;
}
