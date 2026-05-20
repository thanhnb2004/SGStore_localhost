package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.ProductArchivedEvent;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductArchivedConsumer {
    private final IProductIndexingService productIndexingService;

    @RabbitListener(queues = "#{rabbitProperties.getProductArchived().getQueueName()}")
    public void consume(ProductArchivedEvent event) {
        if (event == null || event.getProductId() == null) {
            log.warn("Received null or invalid product archived event, skipping");
            return;
        }
        log.info("Received product archived event for productId={}", event.getProductId());
        productIndexingService.deleteByProductId(event.getProductId());
    }
}
