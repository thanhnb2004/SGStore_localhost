package com.ptit.clone.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTypeResponse {
    private UUID id;
    private String slug;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
