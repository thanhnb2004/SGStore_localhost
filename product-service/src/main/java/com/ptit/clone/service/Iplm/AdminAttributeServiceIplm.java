package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.CreateAttributeRequest;
import com.ptit.clone.dtos.request.UpdateAttributeRequest;
import com.ptit.clone.dtos.response.AttributeDefinitionListResponse;
import com.ptit.clone.dtos.response.AttributeDefinitionResponse;
import com.ptit.clone.entity.AttributeDefinition;
import com.ptit.clone.entity.ProductType;
import com.ptit.clone.mapper.AttributeMapper;
import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.respository.IAttributeDefinitionRepository;
import com.ptit.clone.respository.IProductRepository;
import com.ptit.clone.respository.IProductTypeRepository;
import com.ptit.clone.respository.IProductVariantRepository;
import com.ptit.clone.service.IAdminAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminAttributeServiceIplm implements IAdminAttributeService {

    private final IAttributeDefinitionRepository attributeDefinitionRepository;
    private final IProductTypeRepository productTypeRepository;
    private final IProductRepository productRepository;
    private final IProductVariantRepository productVariantRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public void create(CreateAttributeRequest request) {
        if (attributeDefinitionRepository.findByCodeIgnoreCase(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Attribute code already exists: " + request.getCode());
        }

        ProductType productType = null;
        if (request.getProductTypeId() != null) {
            productType = productTypeRepository.findById(request.getProductTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("ProductType not found: " + request.getProductTypeId()));
        }

        AttributeDefinition definition = new AttributeDefinition();
        definition.setCode(request.getCode() != null ? request.getCode().trim() : null);
        definition.setName(request.getName() != null ? request.getName().trim() : null);
        definition.setScope(request.getScope() != null ? request.getScope() : AttributeScope.PRODUCT);
        definition.setValueType(request.getValueType());
        definition.setFacetable(request.getFacetable() != null && request.getFacetable());
        definition.setFilterPosition(request.getFilterPosition() != null ? request.getFilterPosition() : 0);
        definition.setProductType(productType);

        attributeDefinitionRepository.save(definition);

    }

    @Override
    public void updateAttribute(UUID attributeId, UpdateAttributeRequest request) {
        AttributeDefinition definition = attributeDefinitionRepository.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found: " + attributeId));

        if (request.getName() != null && !request.getName().isBlank()) {
            definition.setName(request.getName().trim());
        }
        if (request.getValueType() != null) {
            definition.setValueType(request.getValueType());
        }
        if (request.getScope() != null) {
            definition.setScope(request.getScope());
        }
        if (request.getFacetable() != null) {
            definition.setFacetable(request.getFacetable());
        }
        if (request.getFilterPosition() != null) {
            definition.setFilterPosition(request.getFilterPosition());
        }
        if (request.getProductTypeId() != null) {
            ProductType productType = productTypeRepository.findById(request.getProductTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("ProductType not found: " + request.getProductTypeId()));
            definition.setProductType(productType);
        }

        attributeDefinitionRepository.save(definition);
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDefinitionListResponse getByProductType(UUID productTypeId, AttributeScope scope) {
        productTypeRepository.findById(productTypeId)
                .orElseThrow(() -> new IllegalArgumentException("ProductType not found: " + productTypeId));

        List<AttributeDefinition> definitions =
                attributeDefinitionRepository.findAllByProductType_IdAndScopeOrderByFilterPositionAsc(productTypeId, scope);
        List<AttributeDefinitionResponse> items = new ArrayList<>();
        for (AttributeDefinition definition : definitions) {
            items.add(attributeMapper.toAttributeResponse(definition));
        }

        return AttributeDefinitionListResponse.builder()
                .items(items)
                .build();
    }

    @Override
    public void deleteAttributes(List<UUID> attributeIds) {
        List<UUID> normalizedAttributeIds = normalizeIds(attributeIds, "Attribute");
        if (productRepository.existsByAttributes_AttributeDefinition_IdIn(normalizedAttributeIds)) {
            throw new IllegalArgumentException("Cannot delete attributes that are assigned to products");
        }
        if (productVariantRepository.existsByOptionValues_AttributeDefinition_IdIn(normalizedAttributeIds)) {
            throw new IllegalArgumentException("Cannot delete attributes that are assigned to variants");
        }

        attributeDefinitionRepository.deleteAllByIdInBatch(normalizedAttributeIds);
    }

    private List<UUID> normalizeIds(List<UUID> ids, String entityName) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(entityName + " ids must not be empty");
        }
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }
}
