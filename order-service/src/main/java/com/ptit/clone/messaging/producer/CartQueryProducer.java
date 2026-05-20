package com.ptit.clone.messaging.producer;

import com.ptit.clone.Handle;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.CartQueryEvent;
import com.ptit.clone.messaging.event.CartQueryResponse;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartQueryProducer implements Handle<CartQueryEvent, CartQueryResponse> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public CartQueryResponse handle(CartQueryEvent event) {
        RabbitConfigProperties props = rabbitProperties.getCartQuery();
        if (props == null) {
            throw new IllegalStateException("RabbitMQ cart-query configuration is missing");
        }

        Object response = rabbitTemplate.convertSendAndReceive(
                props.getExchange(),
                props.getRoutingKey(),
                event
        );

        if (response == null) {
            log.warn("No response received for cart query, userId: {}", event.getUserId());
            return new CartQueryResponse(List.of(), 0L);
        }
        return (CartQueryResponse) response;
    }
}
