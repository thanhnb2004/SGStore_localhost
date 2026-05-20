package com.ptit.clone.controller;

import com.ptit.clone.dtos.request.AddToCartRequest;
import com.ptit.clone.dtos.request.UpdateCartItemRequest;
import com.ptit.clone.dtos.response.CartItemListResponse;
import com.ptit.clone.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    private final ICartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartItemListResponse> addToCart(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddToCartRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.addToCart(userId, request));
    }

    @GetMapping("/")
    public ResponseEntity<CartItemListResponse> getCart(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.getCart(userId));
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-Id") String userId
    ) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable UUID cartItemId
    ) {
        cartService.removeItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartItemListResponse> updateItemQuantity(
            @PathVariable UUID cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.updateItemQuantity(cartItemId, request));
    }
}
