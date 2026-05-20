package com.ptit.clone.dtos.response;

//import com.ptit.clone.entity.CollectionSummaryResponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CollectionListResponse {
    List<CollectionResponse> items;
}
