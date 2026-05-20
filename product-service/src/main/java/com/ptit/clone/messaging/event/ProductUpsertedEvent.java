package com.ptit.clone.messaging.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpsertedEvent {
    private UUID productId;
    private String slug;
    private String name;
    private String shortDescription; //Co the bi null
    private String heroImage; //Co the bi null
    private String status;

    private ProductStatsPayload stats; //Co the bi null
    private LocalDateTime publishedAt; //Co the bi null
    private BrandPayload brand; //Co the bi null
    private ProductTypePayload productType; //Co the bi null
    private List<CollectionPayload> collections; //Co the bi null
    private List<AttributePayload> attributes; //Co the bi null
    private List<VariantPayload> variants; //Co the bi null
}
