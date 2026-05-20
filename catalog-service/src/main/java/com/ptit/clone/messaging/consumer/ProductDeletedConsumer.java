package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.ProductDeletedEvent;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDeletedConsumer {
    private final IProductIndexingService productIndexingService;

    @RabbitListener(queues = "#{rabbitProperties.getProductDeleted().getQueueName()}")
    public void consume(ProductDeletedEvent event) {
        if (event == null || event.getProductId() == null) {
            log.warn("Received null or invalid product deleted event, skipping");
            return;
        }
        log.info("Received product deleted event for productId={}", event.getProductId());
        productIndexingService.deleteByProductId(event.getProductId());
    }
}
