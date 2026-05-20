package com.ptit.clone.messaging.producer;

import com.ptit.clone.Producer;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.ProductUpsertedEvent;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUpsertProducer implements Producer<ProductUpsertedEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public void fire(ProductUpsertedEvent event) {
        RabbitConfigProperties properties = rabbitProperties.getElasticUpsert();
        if (properties == null) {
            throw new IllegalStateException("RabbitMQ elastic-upsert configuration is missing");
        }
        rabbitTemplate.convertAndSend(properties.getExchange(), properties.getRoutingKey(), event);
    }
}
