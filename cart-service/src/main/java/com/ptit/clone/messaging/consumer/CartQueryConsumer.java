package com.ptit.clone.messaging.consumer;

import com.ptit.clone.dtos.response.CartItemListResponse;
import com.ptit.clone.messaging.event.CartItemData;
import com.ptit.clone.messaging.event.CartQueryEvent;
import com.ptit.clone.messaging.event.CartQueryResponse;
import com.ptit.clone.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartQueryConsumer {

    private final ICartService cartService;

    @RabbitListener(queues = "#{rabbitProperties.getCartQuery().getQueueName()}")
    public CartQueryResponse handle(CartQueryEvent request) {
        if (request == null || request.getUserId() == null) {
            log.warn("Received null or empty cart query request");
            return new CartQueryResponse(List.of(), 0L);
        }

        log.info("Received cart query for userId: {}", request.getUserId());

        try {
            CartItemListResponse cartItemListResponse = cartService.getCart(request.getUserId());

            List<CartItemData> items = cartItemListResponse.getItems().stream()
                    .map(item -> new CartItemData(
                            item.getProductId(),
                            item.getVariantId(),
                            item.getProductName(),
                            item.getVariantName(),
                            item.getQuantity(),
                            item.getSalePrice(),
                            item.getVariantHeroImage()
                    ))
                    .toList();

            return new CartQueryResponse(items, cartItemListResponse.getTotalPrice());
        } catch (RuntimeException e) {
            log.warn("Cart not found for userId: {}", request.getUserId());
            return new CartQueryResponse(List.of(), 0L);
        }
    }
}
