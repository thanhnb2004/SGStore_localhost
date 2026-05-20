package com.ptit.clone.dtos.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse {
    private Integer page;
    private Integer size;
    private Long totalItems;
    private Integer totalPages;
}
