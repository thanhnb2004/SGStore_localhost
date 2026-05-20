package com.ptit.clone.messaging.consumer;


import com.ptit.clone.messaging.event.CartVariantQueryEvent;
import com.ptit.clone.messaging.event.CartVariantQueryResponse;
import com.ptit.clone.service.IAdminVariantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartVariantQueryConsumer {

    private final IAdminVariantService variantService;

    @RabbitListener(queues = "#{rabbitProperties.getCartVariantQuery().getQueueName()}")
    public CartVariantQueryResponse handle(CartVariantQueryEvent request) {
        if (request == null || request.getVariantIds() == null || request.getVariantIds().isEmpty()) {
            log.warn("Received null or empty cart variant query request");
            return new CartVariantQueryResponse(List.of());
        }

        log.info("Received cart variant query for {} variants", request.getVariantIds().size());
        return variantService.getVariantEnrichData(request);
    }
}
