package com.ptit.clone.dtos.response;

import com.ptit.clone.model.EntityStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    private UUID brandId;
    private String slug;
    private String name;
    private String logo;
    private EntityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
