package com.ptit.clone.service.Iplm;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ptit.clone.entity.*;
import com.ptit.clone.messaging.event.AttributePayload;
import com.ptit.clone.messaging.event.CollectionPayload;
import com.ptit.clone.messaging.event.ProductUpsertedEvent;
import com.ptit.clone.messaging.event.VariantPayload;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexingServiceIplm implements IProductIndexingService {

    private final ElasticsearchOperations operations;

    @Override
    public void upsert(ProductUpsertedEvent event) {
        if (event == null || event.getProductId() == null) {
            return;
        }

        BrandDocument brandDocument = null;
        if (event.getBrand() != null) {
            brandDocument = BrandDocument.builder()
                    .brandSlug(event.getBrand().getSlug())
                    .brandName(event.getBrand().getName())
                    .brandLogo(event.getBrand().getLogo())
                    .build();
        }

        ProductTypeDocument productTypeDocument = null;
        if (event.getProductType() != null) {
            productTypeDocument = ProductTypeDocument.builder()
                    .productTypeSlug(event.getProductType().getSlug())
                    .productTypeName(event.getProductType().getName())
                    .build();
        }

        List<CollectionDocument> collectionDocuments = new ArrayList<>();
        for (CollectionPayload payload : safeList(event.getCollections())) {
            if (payload == null) continue;
            collectionDocuments.add(CollectionDocument.builder()
                    .collectionSlug(payload.getSlug())
                    .collectionName(payload.getName())
                    .heroBanner(payload.getHeroBanner())
                    .build());
        }

        List<AttributeDocument> attributeDocuments = new ArrayList<>();
        for (AttributePayload payload : safeList(event.getAttributes())) {
            if (payload == null) continue;
            attributeDocuments.add(AttributeDocument.builder()
                    .code(payload.getCode())
                    .name(payload.getName())
                    .value(payload.getValue())
                    .build());
        }

        ProductStatsDocument statsDocument = ProductStatsDocument.builder()
                .ratingAverage(event.getStats() != null ? event.getStats().getRatingAverage() : 0.0)
                .ratingCount(event.getStats() != null ? event.getStats().getRatingCount() : 0)
                .soldCount(event.getStats() != null ? event.getStats().getSoldCount() : 0)
                .build();

        List<ProductDocument> documents = new ArrayList<>();
        for (VariantPayload payload : safeList(event.getVariants())) {
            if (!isSearchableVariant(payload)) {
                continue;
            }

            VariantDocument variantDocument = VariantDocument.builder()
                    .sku(payload.getSku())
                    .variantName(payload.getName())
                    .listPrice(payload.getListPrice())
                    .salePrice(payload.getSalePrice())
                    .availabilityStatus(payload.getAvailabilityStatus())
                    .variantHeroImage(payload.getHeroImage())
                    .optionValues(payload.getOptionValues())
                    .build();

            ProductDocument document = ProductDocument.builder()
                    .variantId(payload.getId())
                    .productId(event.getProductId())
                    .productSlug(event.getSlug())
                    .productName(event.getName())
                    .shortDescription(event.getShortDescription())
                    .productHeroImage(event.getHeroImage())
                    .publishedAt(event.getPublishedAt())
                    .brand(brandDocument)
                    .productType(productTypeDocument)
                    .collections(collectionDocuments)
                    .attributes(attributeDocuments)
                    .variant(variantDocument)
                    .productStats(statsDocument)
                    .build();

            documents.add(document);
        }

        if (!documents.isEmpty()) {
            operations.save(documents);
        }
    }

    @Override
    public void deleteByProductId(UUID productId) {
        if (productId == null) {
            return;
        }
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(q -> q.term(t -> t
                        .field("product_id")
                        .value(productId.toString().toLowerCase()))))
                .build();

        SearchHits<ProductDocument> hits = operations.search(query, ProductDocument.class);
        int deleted = 0;
        for (SearchHit<ProductDocument> hit : hits) {
            operations.delete(hit.getId(), ProductDocument.class);
            deleted++;
        }
        log.info("Deleted {} product document(s) for productId={}", deleted, productId);
    }

    @Override
    public void deleteByVariantId(UUID variantId) {
        if (variantId == null) {
            return;
        }
        String id = variantId.toString();
        ProductDocument existing = operations.get(id, ProductDocument.class);
        if (existing == null) {
            log.info("No product document found for variantId={}, skip delete", variantId);
            return;
        }
        operations.delete(id, ProductDocument.class);
        log.info("Deleted product document for variantId={}", variantId);
    }

    private boolean isSearchableVariant(VariantPayload payload) {
        return payload != null && "ACTIVE".equalsIgnoreCase(payload.getStatus());
    }

    private <T> List<T> safeList(List<T> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        return values;
    }
}


