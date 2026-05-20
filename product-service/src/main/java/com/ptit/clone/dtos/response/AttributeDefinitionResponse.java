package com.ptit.clone.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDefinitionResponse {
    private UUID attributeId;
    private String code;
    private String name;
    private String scope;
    private String valueType;
    private Boolean facetable;
    private Integer filterPosition;
    private String productTypeSlug;
}
