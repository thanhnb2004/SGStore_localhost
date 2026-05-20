package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.VariantPausedEvent;
import com.ptit.clone.service.IProductIndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VariantPausedConsumer {
    private final IProductIndexingService productIndexingService;

    @RabbitListener(queues = "#{rabbitProperties.getVariantPaused().getQueueName()}")
    public void consume(VariantPausedEvent event) {
        if (event == null || event.getVariantId() == null) {
            log.warn("Received null or invalid variant paused event, skipping");
            return;
        }
        log.info("Received variant paused event for variantId={}", event.getVariantId());
        productIndexingService.deleteByVariantId(event.getVariantId());
    }
}
