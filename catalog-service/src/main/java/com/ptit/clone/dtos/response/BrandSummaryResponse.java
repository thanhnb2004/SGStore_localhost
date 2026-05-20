package com.ptit.clone.dtos.response;

import lombok.*;

@Data
@Builder
public class BrandSummaryResponse {
    private String brandSlug;
    private String brandName;
    private String brandLogo;
}
