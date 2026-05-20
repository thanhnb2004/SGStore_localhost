package com.ptit.clone.dtos.request;

import com.ptit.clone.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String slug;
    private String name;
    private String shortDescription;
    private String descriptionHtml;
    private UUID brandId;
    private UUID productTypeId;
    private ProductStatus status;
    private String heroImage;
    private Set<UUID> collectionIds;
    private Map<String, String> productAttributes;
}
