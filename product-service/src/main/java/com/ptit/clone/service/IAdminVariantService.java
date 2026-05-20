package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateVariantRequest;
import com.ptit.clone.dtos.request.UpdateVariantRequest;
import com.ptit.clone.dtos.response.VariantListResponse;
import com.ptit.clone.messaging.event.CartVariantQueryEvent;
import com.ptit.clone.messaging.event.CartVariantQueryResponse;

import java.util.UUID;

public interface IAdminVariantService {
    void createVariant(UUID productId, CreateVariantRequest request);

    void updateVariant(UUID productId, UUID variantId, UpdateVariantRequest request);

    void pauseVariant(UUID productId, UUID variantId);

    void activateVariant(UUID productId, UUID variantId);

    void deleteVariant(UUID productId, UUID variantId);

    VariantListResponse getAllVariants(UUID productId);

    CartVariantQueryResponse getVariantEnrichData (CartVariantQueryEvent request);
}
