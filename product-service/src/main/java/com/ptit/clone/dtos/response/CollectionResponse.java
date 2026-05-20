package com.ptit.clone.dtos.response;


import com.ptit.clone.model.EntityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class CollectionResponse {
    private UUID collectionID;
    private String slug;
    private String name;
    private String descriptionHtml;
    private EntityStatus status;
    private Integer sortOrder;
    private String heroBanner;
    private List<String> facetCodes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
