package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.AvailabilityViewResponse;
import com.ptit.clone.dtos.response.PriceViewResponse;
import com.ptit.clone.dtos.response.ProductVariantResponse;
import com.ptit.clone.entity.ProductVariant;
import com.ptit.clone.entity.ProductVariantOption;
import com.ptit.clone.messaging.event.VariantEnrichData;
import com.ptit.clone.messaging.event.VariantPayload;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class VariantMapper {
    public VariantEnrichData toVariantEnrichData(ProductVariant variant){
        String heroImage;
        if (variant.getHeroImage() != null) {
            heroImage = variant.getHeroImage();
        } else {
            heroImage = variant.getProduct().getHeroImage();
        }

        return VariantEnrichData.builder()
                .variantId(variant.getId())
                .productId(variant.getProduct().getId())
                .productName(variant.getProduct().getName())
                .variantName(variant.getName())
                .heroImage(heroImage)
                .salePrice(variant.getSalePrice())
                .listPrice(variant.getListPrice())
                .build();
    }

    public VariantPayload toVariantPayload(ProductVariant variant) {
        VariantPayload payload = new VariantPayload();
        payload.setId(variant.getId() != null ? variant.getId().toString() : null);
        payload.setSku(variant.getSku());
        payload.setBarcode(variant.getBarcode());
        payload.setName(variant.getName());
        payload.setStatus(variant.getStatus() != null ? variant.getStatus().name() : null);
        payload.setListPrice(variant.getListPrice());
        payload.setSalePrice(variant.getSalePrice());
        payload.setAvailabilityStatus(variant.getAvailabilityStatus() != null ? variant.getAvailabilityStatus().name() : null);
        payload.setHeroImage(variant.getHeroImage());
        payload.setImages(variant.getImages());
        payload.setOptionValues(toVariantOptionValueMap(variant));
        return payload;
    }

    public ProductVariantResponse toProductVariantResponse(ProductVariant variant) {
        PriceViewResponse price = PriceViewResponse.builder()
                .listPrice(variant.getListPrice())
                .salePrice(variant.getSalePrice())
                .build();

        AvailabilityViewResponse availability = AvailabilityViewResponse.builder()
                .status(variant.getAvailabilityStatus() != null ? variant.getAvailabilityStatus().name() : null)
                .variantStatus(variant.getStatus() != null ? variant.getStatus().name() : null)
                .build();

        return ProductVariantResponse.builder()
                .variantId(variant.getId())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .name(variant.getName())
                .optionValues(toVariantOptionValueMap(variant))
                .price(price)
                .availability(availability)
                .heroImage(variant.getHeroImage())
                .build();
    }

    private Map<String, String> toVariantOptionValueMap(ProductVariant variant) {
        if (variant == null || variant.getOptionValues() == null || variant.getOptionValues().isEmpty()) {
            return Map.of();
        }

        Map<String, String> optionValues = new LinkedHashMap<>();
        for (ProductVariantOption option : variant.getOptionValues()) {
            if (option.getAttributeDefinition() != null && option.getAttributeDefinition().getCode() != null) {
                optionValues.put(option.getAttributeDefinition().getCode(), option.getValue());
            }
        }
        return optionValues;
    }
}
