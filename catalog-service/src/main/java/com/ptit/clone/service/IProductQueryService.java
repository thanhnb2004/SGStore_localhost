package com.ptit.clone.service;

import com.ptit.clone.dtos.response.ProductSummaryResponse;
import com.ptit.clone.dtos.response.ProductSummaryListResponse;

import java.util.List;

public interface IProductQueryService {
    ProductSummaryListResponse search(String q, Integer page, Integer size, String sortBy);
    List<ProductSummaryResponse> getBestSellers (String category, Integer limit);
}
