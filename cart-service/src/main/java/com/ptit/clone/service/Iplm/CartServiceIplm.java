package com.ptit.clone.service.Iplm;

import com.ptit.clone.dtos.request.AddToCartRequest;
import com.ptit.clone.dtos.request.UpdateCartItemRequest;
import com.ptit.clone.dtos.response.CartItemResponse;
import com.ptit.clone.dtos.response.CartItemListResponse;
import com.ptit.clone.entity.Cart;
import com.ptit.clone.entity.CartItem;
import com.ptit.clone.messaging.event.CartVariantQueryEvent;
import com.ptit.clone.messaging.event.CartVariantQueryResponse;
import com.ptit.clone.messaging.event.VariantEnrichData;
import com.ptit.clone.messaging.producer.QueryVariantsProducer;
import com.ptit.clone.respository.ICartItemRepository;
import com.ptit.clone.respository.ICartRepository;
import com.ptit.clone.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceIplm implements ICartService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final QueryVariantsProducer variantsProducer;

    @Override
    @Transactional
    public CartItemListResponse addToCart(String userId, AddToCartRequest request) {
        List<UUID> variantIdList = new ArrayList<>();
        variantIdList.add(request.getVariantId());

        CartVariantQueryEvent queryEvent = new CartVariantQueryEvent(variantIdList);
        CartVariantQueryResponse variantInfo = variantsProducer.handle(queryEvent);

        if (variantInfo.getVariants().isEmpty()) {
            throw new RuntimeException("Variant not found: " + request.getVariantId());
        }

        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            cart = cartRepository.save(newCart);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndVariantId(cart.getId(), request.getVariantId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(request.getProductId());
            newItem.setVariantId(request.getVariantId());
            newItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newItem);
        }

        return getCart(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemListResponse getCart(String userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            return CartItemListResponse.builder()
                    .userId(userId)
                    .items(List.of())
                    .totalItems(0)
                    .totalQuantity(0)
                    .totalPrice(0L)
                    .build();
        }
        Cart cart = cartOptional.get();

        List<UUID> variantIds = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            variantIds.add(item.getVariantId());
        }

        Map<UUID, VariantEnrichData> enrichMap = new HashMap<>();
        if (!variantIds.isEmpty()) {
            CartVariantQueryEvent queryEvent = new CartVariantQueryEvent(variantIds);
            CartVariantQueryResponse response = variantsProducer.handle(queryEvent);
            for (VariantEnrichData enrich : response.getVariants()) {
                enrichMap.put(enrich.getVariantId(), enrich);
            }
        }

        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            VariantEnrichData enrich = enrichMap.get(item.getVariantId());

            CartItemResponse.CartItemResponseBuilder builder = CartItemResponse.builder();
            builder.cartItemId(item.getId());
            builder.productId(item.getProductId());
            builder.variantId(item.getVariantId());
            builder.quantity(item.getQuantity());

            if (enrich != null) {
                builder.productName(enrich.getProductName());
                builder.variantName(enrich.getVariantName());
                builder.variantHeroImage(enrich.getHeroImage());
                builder.salePrice(enrich.getSalePrice());
                builder.listPrice(enrich.getListPrice());
            } else {
                builder.productName(null);
                builder.variantName(null);
                builder.variantHeroImage(null);
                builder.salePrice(null);
                builder.listPrice(null);
            }

            itemResponses.add(builder.build());
        }

        int totalItems = itemResponses.size();

        int totalQuantity = 0;
        for (CartItemResponse item : itemResponses) {
            totalQuantity = totalQuantity + item.getQuantity();
        }

        long totalPrice = 0;
        for (CartItemResponse item : itemResponses) {
            if (item.getSalePrice() != null) {
                totalPrice = totalPrice + (item.getSalePrice() * item.getQuantity());
            }
        }

        CartItemListResponse response = CartItemListResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalItems(totalItems)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .build();

        return response;
    }

    @Override
    @Transactional
    public void removeItem(UUID cartItemId) {
        Optional<CartItem> itemOptional = cartItemRepository.findById(cartItemId);
        if (itemOptional.isEmpty()) {
            throw new RuntimeException("Cart item not found: " + cartItemId);
        }
        CartItem item = itemOptional.get();
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public CartItemListResponse updateItemQuantity(UUID cartItemId, UpdateCartItemRequest request) {
        Optional<CartItem> itemOptional = cartItemRepository.findById(cartItemId);
        if (itemOptional.isEmpty()) {
            throw new RuntimeException("Cart item not found: " + cartItemId);
        }
        CartItem item = itemOptional.get();

        List<UUID> variantIdList = new ArrayList<>();
        variantIdList.add(item.getVariantId());

        CartVariantQueryEvent queryEvent = new CartVariantQueryEvent(variantIdList);
        CartVariantQueryResponse variantInfo = variantsProducer.handle(queryEvent);

        if (variantInfo.getVariants().isEmpty()) {
            throw new RuntimeException("Variant not found: " + item.getVariantId());
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        String userId = item.getCart().getUserId();
        return getCart(userId);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        Optional<Cart> cartOptional = cartRepository.findByUserId(userId);
        if (cartOptional.isEmpty()) {
            throw new RuntimeException("Cart not found for user: " + userId);
        }
        Cart cart = cartOptional.get();
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
