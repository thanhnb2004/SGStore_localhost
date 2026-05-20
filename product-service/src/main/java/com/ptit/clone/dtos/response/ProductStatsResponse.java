package com.ptit.clone.dtos.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductStatsResponse {
    private Double ratingAverage;
    private Integer ratingCount;
    private Integer soldCount;
}
