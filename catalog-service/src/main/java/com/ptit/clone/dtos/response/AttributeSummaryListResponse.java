package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AttributeSummaryListResponse {
    private List<AttributeSummaryResponse> items;
}
