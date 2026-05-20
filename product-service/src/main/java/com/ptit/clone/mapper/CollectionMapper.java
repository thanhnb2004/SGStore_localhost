package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.CollectionResponse;
import com.ptit.clone.entity.ProductCollection;
import com.ptit.clone.messaging.event.CollectionPayload;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionPayload toCollectionPayload(ProductCollection collection) {
        return new CollectionPayload(
                collection.getId().toString(),
                collection.getSlug(),
                collection.getName(),
                collection.getHeroBanner()
        );
    }

    public CollectionResponse toCollectionResponse(ProductCollection collection) {
        return CollectionResponse.builder()
                .collectionID(collection.getId())
                .name(collection.getName())
                .slug(collection.getSlug())
                .sortOrder(collection.getSortOrder())
                .heroBanner(collection.getHeroBanner())
                .descriptionHtml(collection.getDescriptionHtml())
                .facetCodes(collection.getFacetCodes())
                .status(collection.getStatus())
                .createdAt(collection.getCreatedAt())
                .updatedAt(collection.getUpdatedAt())
                .build();
    }
}
