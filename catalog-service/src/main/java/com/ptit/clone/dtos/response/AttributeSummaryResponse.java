package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeSummaryResponse {
    private String name;
    private String value;
}
