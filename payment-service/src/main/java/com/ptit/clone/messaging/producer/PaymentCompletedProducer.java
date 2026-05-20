package com.ptit.clone.messaging.producer;

import com.ptit.clone.Producer;
import com.ptit.clone.config.properties.RabbitProperties;
import com.ptit.clone.messaging.event.PaymentCompletedEvent;
import com.ptit.clone.properties.RabbitConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedProducer implements Producer<PaymentCompletedEvent> {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public void fire(PaymentCompletedEvent event) {
        RabbitConfigProperties props = rabbitProperties.getPaymentCompleted();
        if (props == null) {
            log.warn("RabbitMQ payment-completed configuration is missing, skipping event publish");
            return;
        }
        rabbitTemplate.convertAndSend(props.getExchange(), props.getRoutingKey(), event);
    }
}
