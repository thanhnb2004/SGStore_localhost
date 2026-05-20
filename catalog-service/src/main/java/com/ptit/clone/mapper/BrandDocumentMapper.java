package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.BrandSummaryResponse;
import com.ptit.clone.entity.BrandDocument;
import com.ptit.clone.messaging.event.BrandPayload;
import org.springframework.stereotype.Component;

@Component
public class BrandDocumentMapper {

    public BrandSummaryResponse toBrandSummaryResponse(BrandDocument brandDocument) {
        if (brandDocument == null) {
            return null;
        }

        return BrandSummaryResponse.builder()
                .brandSlug(brandDocument.getBrandSlug())
                .brandName(brandDocument.getBrandName())
                .brandLogo(brandDocument.getBrandLogo())
                .build();
    }
}
