package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.BrandResponse;
import com.ptit.clone.entity.Brand;
import com.ptit.clone.messaging.event.BrandPayload;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public BrandPayload toBrandPayload(Brand brand) {
        return new BrandPayload(
                brand.getId().toString(),
                brand.getSlug(),
                brand.getName(),
                brand.getLogo()
        );
    }

    public BrandResponse toBrandResponse (Brand brand){
        return BrandResponse.builder()
                .brandId(brand.getId())
                .slug(brand.getSlug())
                .name(brand.getName())
                .logo(brand.getLogo())
                .status(brand.getStatus())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
