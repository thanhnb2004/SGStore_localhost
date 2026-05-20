package com.ptit.clone.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CartItemListResponse {
    private UUID cartId;
    private String userId;
    private List<CartItemResponse> items;
    private Integer totalItems;       // tổng số dòng sản phẩm khác nhau
    private Integer totalQuantity;    // tổng số lượng tất cả items
    private Long totalPrice;      // tổng tiền (salePrice × quantity)
}
