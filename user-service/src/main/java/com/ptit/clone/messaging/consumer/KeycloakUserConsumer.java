package com.ptit.clone.messaging.consumer;

import com.ptit.clone.messaging.event.KeycloakEmailVerifiedEvent;
import com.ptit.clone.service.IRegisterMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserConsumer {
    private final IRegisterMemberService registerMemberService;

    @RabbitListener(queues = "#{rabbitProperties.getMarkVerified().getQueueName()}")
    public void consumeEmailVerifiedEvent(KeycloakEmailVerifiedEvent event){
        if (event == null || event.getKeycloakUserId() == null || event.getKeycloakUserId().isBlank()) {
            log.warn("Received invalid KeycloakEmailVerifiedEvent: {}", event);
            return;
        }

        registerMemberService.markMemberEmailVerified(event.getKeycloakUserId());
        log.info("Member marked ACTIVE from verify-email event, keycloakUserId={}", event.getKeycloakUserId());
    }
}
//start
