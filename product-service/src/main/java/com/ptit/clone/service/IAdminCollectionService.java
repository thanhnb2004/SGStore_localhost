package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateCollectionRequest;
import com.ptit.clone.dtos.request.UpdateCollectionRequest;
import com.ptit.clone.dtos.response.CollectionListResponse;

import java.util.List;
import java.util.UUID;

public interface IAdminCollectionService {
    void createCollection(CreateCollectionRequest request);

    void updateCollection(UUID collectionId, UpdateCollectionRequest request);

    CollectionListResponse listCollections();

    void deleteCollections(List<UUID> collectionIds);
}
