package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.ProductSummaryResponse;
import com.ptit.clone.entity.ProductDocument;
import com.ptit.clone.entity.VariantDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDocumentMapper {

    private final AttributeDocumentMapper attributeDocumentMapper;
    private final BrandDocumentMapper brandDocumentMapper;
    private final ProductStatsMapper productStatsMapper;
    private final ProductTypeDocumentMapper productTypeDocumentMapper;
    private final VariantDocumentMapper variantDocumentMapper;

    public ProductSummaryResponse toSearchProductItemResponse(ProductDocument document) {
        return toProductSummaryResponse(document);
    }

    public ProductSummaryResponse toProductSummaryResponse(ProductDocument document) {
        if (document == null) {
            return null;
        }

        VariantDocument variant = document.getVariant();

        return ProductSummaryResponse.builder()
                .productId(document.getProductId())
                .productSlug(document.getProductSlug())
                .productHeroImage(document.getProductHeroImage())
                .variantId(document.getVariantId())
                .brand(brandDocumentMapper.toBrandSummaryResponse(document.getBrand()))
                .productType(productTypeDocumentMapper.toProductTypeSummaryResponse(document.getProductType()))
                .productStats(productStatsMapper.toProductStatsSummaryResponse(document.getProductStats()))
                .attributes(attributeDocumentMapper.toAttributeSummaryListResponse(document.getAttributes()))
                .variant(variantDocumentMapper.toVariantSummaryResponse(document.getVariant()))
                .discountPercent(null)
                .badgeLabels(List.of())
                .build();
    }
}
