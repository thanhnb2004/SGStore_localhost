package com.ptit.clone.dtos.request;

import com.ptit.clone.model.EntityStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBrandRequest {
    private String name;
    private String logo;
    private EntityStatus status;
}
