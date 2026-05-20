package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.ProductUpsertedEvent;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUpsertConsumer {
    private final IProductIndexingService productIndexingService;

    @RabbitListener(queues = "#{rabbitProperties.getElasticUpsert().getQueueName()}")
    public void consume(ProductUpsertedEvent event) {
        if (event == null || event.getProductId() == null) {
            log.warn("Received null or invalid product upsert event, skipping");
            return;
        }
        log.info("Received product upsert event for productId={}", event.getProductId());
        productIndexingService.upsert(event);
    }
}
