package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.CartClearEvent;
import com.ptit.clone.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartClearConsumer {

    private final ICartService cartService;

    @RabbitListener(queues = "#{rabbitProperties.getCartClear().getQueueName()}")
    public void handle(CartClearEvent event) {
        if (event == null || event.getUserId() == null) {
            log.warn("Received null or empty cart clear event");
            return;
        }

        log.info("Clearing cart for userId: {}", event.getUserId());

        try {
            cartService.clearCart(event.getUserId());
        } catch (RuntimeException e) {
            log.warn("Failed to clear cart for userId: {}", event.getUserId());
        }
    }
}
