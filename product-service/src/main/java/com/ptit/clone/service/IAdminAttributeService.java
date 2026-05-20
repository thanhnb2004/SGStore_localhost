package com.ptit.clone.service;

import com.ptit.clone.dtos.request.CreateAttributeRequest;
import com.ptit.clone.dtos.request.UpdateAttributeRequest;
import com.ptit.clone.dtos.response.AttributeDefinitionListResponse;
import com.ptit.clone.model.AttributeScope;

import java.util.List;
import java.util.UUID;

public interface IAdminAttributeService {
    void create(CreateAttributeRequest request);
    void updateAttribute(UUID attributeId, UpdateAttributeRequest request);
    AttributeDefinitionListResponse getByProductType(UUID productTypeId, AttributeScope scope);
    void deleteAttributes(List<UUID> attributeIds);
}
