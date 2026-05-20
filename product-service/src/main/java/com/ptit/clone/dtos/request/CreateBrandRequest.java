package com.ptit.clone.dtos.request;

import com.ptit.clone.model.EntityStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBrandRequest {

    @NotBlank
    private String name;

    private String logo;

    private EntityStatus status;
}
