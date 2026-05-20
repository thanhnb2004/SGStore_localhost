package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.ProductStatsSummaryResponse;
import com.ptit.clone.entity.ProductStatsDocument;
import com.ptit.clone.messaging.event.ProductStatsPayload;
import org.springframework.stereotype.Component;

@Component
public class ProductStatsMapper {

    public ProductStatsSummaryResponse toProductStatsSummaryResponse(ProductStatsDocument document) {
        if (document == null) {
            return null;
        }
        return ProductStatsSummaryResponse.builder()
                .ratingAverage(document.getRatingAverage())
                .ratingCount(document.getRatingCount())
                .soldCount(document.getSoldCount())
                .build();
    }
}
