package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.ProductTypeResponse;
import com.ptit.clone.entity.ProductType;
import com.ptit.clone.messaging.event.ProductTypePayload;
import org.springframework.stereotype.Component;

@Component
public class ProductTypeMapper {
    public ProductTypePayload toProductTypePayload(ProductType productType) {
        return new ProductTypePayload(
                productType.getId().toString(),
                productType.getSlug(),
                productType.getName()
        );
    }

    public ProductTypeResponse toProductTypeDetailResponse (ProductType productType){
        return ProductTypeResponse.builder()
                .id(productType.getId())
                .name(productType.getName())
                .slug(productType.getSlug())
                .createdAt(productType.getCreatedAt())
                .updatedAt(productType.getUpdatedAt())
                .build();
    }
}
