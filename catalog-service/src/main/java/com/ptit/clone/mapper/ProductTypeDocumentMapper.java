package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.ProductTypeSummaryResponse;
import com.ptit.clone.entity.ProductTypeDocument;
import com.ptit.clone.messaging.event.ProductTypePayload;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeDocumentMapper {


    public ProductTypeSummaryResponse toProductTypeSummaryResponse(ProductTypeDocument document) {
        if (document == null) {
            return null;
        }

        return ProductTypeSummaryResponse.builder()
                .productTypeSlug(document.getProductTypeSlug())
                .productTypeName(document.getProductTypeName())
                .build();
    }

}
