package com.ptit.clone.dtos.response;

import lombok.*;

@Data
@Builder
public class ProductStatsSummaryResponse {
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer soldCount;
}
