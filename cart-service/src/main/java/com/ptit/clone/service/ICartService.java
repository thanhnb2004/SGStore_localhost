package com.ptit.clone.service;

import com.ptit.clone.dtos.request.AddToCartRequest;
import com.ptit.clone.dtos.request.UpdateCartItemRequest;
import com.ptit.clone.dtos.response.CartItemListResponse;

import java.util.UUID;

public interface ICartService {
    CartItemListResponse addToCart(String userId, AddToCartRequest request);
    CartItemListResponse getCart(String userId);
    void removeItem(UUID cartItemId);
    CartItemListResponse updateItemQuantity(UUID cartItemId, UpdateCartItemRequest request);
    void clearCart(String userId);
}
