package com.ptit.clone.service;

import com.ptit.clone.messaging.event.ProductUpsertedEvent;

import java.util.UUID;

public interface IProductIndexingService  {
    void upsert (ProductUpsertedEvent event);
    void deleteByProductId(UUID productId);
    void deleteByVariantId(UUID variantId);
}
