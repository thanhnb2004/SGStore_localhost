package com.ptit.clone.dtos.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSummaryListResponse {
    private List<ProductSummaryResponse> items;
    private PageResponse pagination;
//    private Map<String, Object> appliedFilters;
//    private String sortBy;
}

