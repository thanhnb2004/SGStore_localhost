package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.VariantSummaryResponse;
import com.ptit.clone.entity.VariantDocument;
import com.ptit.clone.messaging.event.VariantPayload;
import org.springframework.stereotype.Component;

@Component
public class VariantDocumentMapper {

    public VariantSummaryResponse toVariantSummaryResponse(VariantDocument document) {
        if (document == null) {
            return null;
        }
        return VariantSummaryResponse.builder()
                .sku(document.getSku())
                .variantName(document.getVariantName())
                .listPrice(document.getListPrice())
                .salePrice(document.getSalePrice())
                .variantHeroImage(document.getVariantHeroImage())
                .availabilityStatus(document.getAvailabilityStatus())
                .build();
    }

}
