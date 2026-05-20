package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.CartClearEvent;
import com.ptit.clone.messaging.event.PaymentCompletedEvent;
import com.ptit.clone.messaging.producer.CartClearProducer;
import com.ptit.clone.model.OrderStatus;
import com.ptit.clone.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final IOrderService orderService;
    private final CartClearProducer cartClearProducer;

    @RabbitListener(queues = "#{rabbitProperties.getPaymentCompleted().getQueueName()}")
    public void handle(PaymentCompletedEvent event) {
        log.info("Payment completed for orderId: {}, userId: {}", event.getOrderId(), event.getUserId());

        orderService.updateStatus(event.getOrderId(), OrderStatus.CONFIRMED);

        cartClearProducer.fire(new CartClearEvent(event.getUserId()));

        log.info("Order {} confirmed and cart cleared for userId: {}", event.getOrderId(), event.getUserId());
    }
}
