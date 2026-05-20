package com.ptit.clone.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpsertedEvent {
    private String productId;
    private String slug;
    private String name;
    private String shortDescription;
    private String heroImage;
    private String status;

    private ProductStatsPayload stats; //Co the bi null

    private LocalDateTime publishedAt; //Co the bi null

    private BrandPayload brand;
    private ProductTypePayload productType;
    private List<CollectionPayload> collections;
    private List<AttributePayload> attributes;
    private List<VariantPayload> variants;
}
