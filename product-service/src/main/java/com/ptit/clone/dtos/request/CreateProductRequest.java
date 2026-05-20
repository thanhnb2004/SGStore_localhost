package com.ptit.clone.dtos.request;

import com.ptit.clone.model.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {

    @NotBlank
    private String name;
    private String shortDescription;
    private String descriptionHtml;
    private String heroImage;
    @NotNull
    private ProductStatus status;
    @NotNull
    private UUID brandId;
    @NotNull
    private UUID productTypeId;
    private Set<UUID> collectionIds;
    private Map<String, String> productAttributes;
    //Key: code của AttributeDefi; Value: gia trị của attributeValue

    @NotNull
    @Valid
    private CreateVariantRequest defaultVariant;
}
