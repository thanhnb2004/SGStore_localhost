package com.ptit.clone.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityViewResponse {
    private String status;        // IN_STOCK | LOW_STOCK | OUT_OF_STOCK | PRE_ORDER
    private String variantStatus; // ACTIVE | INACTIVE
}
