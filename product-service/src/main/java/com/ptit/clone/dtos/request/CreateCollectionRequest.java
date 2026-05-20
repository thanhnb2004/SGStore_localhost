package com.ptit.clone.dtos.request;

import com.ptit.clone.model.EntityStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCollectionRequest {

    @NotBlank
    private String name;
    private String descriptionHtml;
    private EntityStatus status;
    private Integer sortOrder;
    private String heroBanner;
    private List<String> facetCodes;

}
