package com.ptit.clone.mapper;


import com.ptit.clone.dtos.response.AdminVariantDetailResponse;
import com.ptit.clone.dtos.response.BrandResponse;
import com.ptit.clone.dtos.response.CollectionListResponse;
import com.ptit.clone.dtos.response.CollectionResponse;
import com.ptit.clone.dtos.response.ProductDetailResponse;
import com.ptit.clone.dtos.response.ProductResponse;
import com.ptit.clone.dtos.response.ProductStatsResponse;
import com.ptit.clone.dtos.response.ProductTypeResponse;
import com.ptit.clone.dtos.response.ProductVariantResponse;
import com.ptit.clone.dtos.response.VariantListResponse;
import com.ptit.clone.entity.*;
import com.ptit.clone.messaging.event.*;

import com.ptit.clone.model.AttributeScope;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final VariantMapper variantMapper;
    private final AttributeMapper attributeMapper;
    private final BrandMapper brandMapper;
    private final ProductTypeMapper productTypeMapper;
    private final CollectionMapper collectionMapper;

    public ProductUpsertedEvent toProductUpsertedEvent(Product product) {
        Set<ProductVariant> variants = product.getVariants() != null ? product.getVariants() : Set.of();
        Set<ProductCollection> collections = product.getCollections() != null ? product.getCollections() : Set.of();
        Set<ProductAttributeValue> attributes = product.getAttributes() != null ? product.getAttributes() : Set.of();

        ProductUpsertedEvent event = new ProductUpsertedEvent();
        event.setProductId(product.getId());
        event.setSlug(product.getSlug());
        event.setName(product.getName());
        event.setShortDescription(product.getShortDescription());
        event.setHeroImage(product.getHeroImage());
        event.setStatus(product.getStatus() != null ? product.getStatus().name() : null);
        event.setStats(toProductStatsPayload(product.getStats()));
        event.setPublishedAt(product.getPublishedAt());
        event.setBrand(product.getBrand() != null ? brandMapper.toBrandPayload(product.getBrand()) : null);
        event.setProductType(product.getProductType() != null ? productTypeMapper.toProductTypePayload(product.getProductType()) : null);
        event.setCollections(collections.stream().map(collectionMapper::toCollectionPayload).toList());
        event.setAttributes(attributes.stream().map(attributeMapper::toAttributePayload).toList());
        event.setVariants(variants.stream().map(variantMapper::toVariantPayload).toList());
        return event;
    }

    private ProductStatsPayload toProductStatsPayload(ProductStats stats) {
        ProductStatsPayload payload = new ProductStatsPayload();
        payload.setRatingAverage(stats != null ? stats.getRatingAverage() : 0.0);
        payload.setRatingCount(stats != null ? stats.getRatingCount() : 0);
        payload.setSoldCount(stats != null ? stats.getSoldCount() : 0);
        return payload;
    }

    public ProductResponse toProductResponse(Product product) {
        ProductStatsResponse statsResponse = null;
        if (product.getStats() != null) {
            ProductStats stats = product.getStats();
            statsResponse = ProductStatsResponse.builder()
                    .ratingAverage(stats.getRatingAverage())
                    .ratingCount(stats.getRatingCount())
                    .soldCount(stats.getSoldCount())
                    .build();
        }

        return ProductResponse.builder()
                .productId(product.getId())
                .slug(product.getSlug())
                .name(product.getName())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .heroImage(product.getHeroImage())
                .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
                .productTypeName(product.getProductType() != null ? product.getProductType().getName() : null)
                .variantCount(product.getVariants() != null ? product.getVariants().size() : 0)
                .productStats(statsResponse)
                .collectionCount(product.getCollections() != null ? product.getCollections().size() : 0)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .publishedAt(product.getPublishedAt())
                .build();
    }

    public AdminVariantDetailResponse toAdminVariantDetailResponse(ProductVariant variant) {
        return AdminVariantDetailResponse.builder()
                .variantId(variant.getId())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .name(variant.getName())
                .status(variant.getStatus() != null ? variant.getStatus().name() : null)
                .listPrice(variant.getListPrice())
                .salePrice(variant.getSalePrice())
                .availabilityStatus(variant.getAvailabilityStatus() != null ? variant.getAvailabilityStatus().name() : null)
                .heroImage(variant.getHeroImage())
                .images(variant.getImages())
                .optionValues(toVariantOptionValueMap(variant))
                .build();
    }

    public ProductDetailResponse toProductDetailResponse(Product product) {
        // attributes (scope=PRODUCT)
        Map<String, String> attributes = new LinkedHashMap<>();
        Set<ProductAttributeValue> attrs = product.getAttributes() != null ? product.getAttributes() : Set.of();
        for (ProductAttributeValue attr : attrs) {
            if (attr.getAttributeDefinition() != null
                    && attr.getAttributeDefinition().getScope() == AttributeScope.PRODUCT) {
                attributes.put(attr.getAttributeDefinition().getCode(), attr.getValue());
            }
        }

        // productType
        ProductTypeResponse productTypeResponse = product.getProductType() == null ? null
                : productTypeMapper.toProductTypeDetailResponse(product.getProductType());

        // collections
        List<CollectionResponse> collectionItems = new ArrayList<>();
        if (product.getCollections() != null) {
            for (ProductCollection c : product.getCollections()) {
                collectionItems.add(collectionMapper.toCollectionResponse(c));
            }
        }
        CollectionListResponse collectionListResponse = CollectionListResponse.builder()
                .items(collectionItems)
                .build();

        // variants
        List<ProductVariantResponse> variantItems = new ArrayList<>();
        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                variantItems.add(variantMapper.toProductVariantResponse(variant));
            }
        }
        VariantListResponse variantListResponse = VariantListResponse.builder()
                .items(variantItems)
                .build();

        // brand
        BrandResponse brandDetail = product.getBrand() == null ? null
                : brandMapper.toBrandResponse(product.getBrand());

        return ProductDetailResponse.builder()
                .productId(product.getId())
                .slug(product.getSlug())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .descriptionHtml(product.getDescriptionHtml())
                .status(product.getStatus() != null ? product.getStatus().name() : null)
                .heroImage(product.getHeroImage())
                .brand(brandDetail)
                .productTypeResponse(productTypeResponse)
                .collectionListResponse(collectionListResponse)
                .attributes(attributes)
                .variantListResponse(variantListResponse)
                .publishedAt(product.getPublishedAt())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
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
