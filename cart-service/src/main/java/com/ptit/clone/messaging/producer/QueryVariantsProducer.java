package com.ptit.clone.messaging.producer;

import com.ptit.clone.Handle;
import com.ptit.clone.Producer;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.CartVariantQueryEvent;
import com.ptit.clone.messaging.event.CartVariantQueryResponse;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class QueryVariantsProducer implements Handle<CartVariantQueryEvent, CartVariantQueryResponse> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public CartVariantQueryResponse handle(CartVariantQueryEvent event) {
        RabbitConfigProperties props = rabbitProperties.getCartVariantQuery();
        if (props == null) {
            throw new IllegalStateException("RabbitMQ cart-variant-query configuration is missing");
        }


        Object response = rabbitTemplate.convertSendAndReceive(
                props.getExchange(),
                props.getRoutingKey(),
                event
        );

        if (response == null) {
            log.warn("No response received for cart variant query");
            return new CartVariantQueryResponse(List.of());
        }
        return (CartVariantQueryResponse) response;
    }
}
