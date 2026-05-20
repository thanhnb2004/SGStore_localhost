package com.ptit.clone.dtos.request;

import com.ptit.clone.model.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCollectionRequest {
    private String slug;
    private String name;
    private String descriptionHtml;
    private String heroBanner;
    private List<String> facetCodes;
    private EntityStatus status;
    private Integer sortOrder;
}
