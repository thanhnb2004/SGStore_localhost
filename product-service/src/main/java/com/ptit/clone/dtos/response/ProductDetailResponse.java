package com.ptit.clone.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private UUID productId;
    private String slug;
    private String name;
    private String shortDescription;
    private String descriptionHtml;
    private String status;
    private String heroImage;
    private BrandResponse brand;
    private ProductTypeResponse productTypeResponse;
    private CollectionListResponse collectionListResponse;
    private Map<String, String> attributes;
    private VariantListResponse variantListResponse;

    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
