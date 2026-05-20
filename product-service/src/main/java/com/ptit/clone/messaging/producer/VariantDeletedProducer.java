package com.ptit.clone.messaging.producer;

import com.ptit.clone.Producer;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.VariantDeletedEvent;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VariantDeletedProducer implements Producer<VariantDeletedEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public void fire(VariantDeletedEvent event) {
        RabbitConfigProperties properties = rabbitProperties.getVariantDeleted();
        if (properties == null) {
            throw new IllegalStateException("RabbitMQ variant-deleted configuration is missing");
        }
        rabbitTemplate.convertAndSend(properties.getExchange(), properties.getRoutingKey(), event);
    }
}
