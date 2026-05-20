package com.ptit.clone.dtos.request;

import com.ptit.clone.model.AttributeScope;
import com.ptit.clone.model.AttributeValueType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAttributeRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private AttributeValueType valueType;

    private AttributeScope scope;

    private Boolean facetable;

    @Min(0)
    private Integer filterPosition;

    private UUID productTypeId;
}
