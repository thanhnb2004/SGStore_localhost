package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.VariantDeletedEvent;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariantDeletedConsumer {
    private final IProductIndexingService productIndexingService;

    @RabbitListener(queues = "#{rabbitProperties.getVariantDeleted().getQueueName()}")
    public void consume(VariantDeletedEvent event) {
        if (event == null || event.getVariantId() == null) {
            log.warn("Received null or invalid variant deleted event, skipping");
            return;
        }
        log.info("Received variant deleted event for variantId={}", event.getVariantId());
        productIndexingService.deleteByVariantId(event.getVariantId());
    }
}
