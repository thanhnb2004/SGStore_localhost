package com.ptit.clone.messaging.producer;

import com.ptit.clone.Producer;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.ProductArchivedEvent;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductArchivedProducer implements Producer<ProductArchivedEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public void fire(ProductArchivedEvent event) {
        RabbitConfigProperties properties = rabbitProperties.getProductArchived();
        if (properties == null) {
            throw new IllegalStateException("RabbitMQ product-archived configuration is missing");
        }
        rabbitTemplate.convertAndSend(properties.getExchange(), properties.getRoutingKey(), event);
    }
}
