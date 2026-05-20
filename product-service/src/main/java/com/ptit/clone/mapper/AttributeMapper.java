package com.ptit.clone.mapper;

import com.ptit.clone.dtos.response.AttributeDefinitionResponse;
import com.ptit.clone.entity.AttributeDefinition;
import com.ptit.clone.entity.ProductAttributeValue;
import com.ptit.clone.messaging.event.AttributePayload;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {
    public AttributePayload toAttributePayload(ProductAttributeValue attribute) {
        AttributePayload payload = new AttributePayload();
        if (attribute.getAttributeDefinition() != null) {
            payload.setCode(attribute.getAttributeDefinition().getCode());
            payload.setName(attribute.getAttributeDefinition().getName());
            payload.setFacetable(attribute.getAttributeDefinition().getFacetable());
        }
        payload.setValue(attribute.getValue());
        return payload;
    }

    public AttributeDefinitionResponse toAttributeResponse(AttributeDefinition definition) {
        return AttributeDefinitionResponse.builder()
                .attributeId(definition.getId())
                .code(definition.getCode())
                .name(definition.getName())
                .scope(definition.getScope() != null ? definition.getScope().name() : null)
                .valueType(definition.getValueType() != null ? definition.getValueType().name() : null)
                .facetable(definition.getFacetable())
                .filterPosition(definition.getFilterPosition() == null ? 0 : definition.getFilterPosition())
                .productTypeSlug(definition.getProductType() != null ? definition.getProductType().getSlug() : null)
                .build();
    }
}
