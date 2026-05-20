package com.ptit.clone.keycloak;

import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class KeycloakVerifyEmailEventListenerProvider implements EventListenerProvider {
    private final RabbitEventPublisher publisher;

    public KeycloakVerifyEmailEventListenerProvider(RabbitEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onEvent(Event event) {
        if (event == null || event.getType() != EventType.CUSTOM_REQUIRED_ACTION) {
            return;
        }
        try {
            KeycloakEmailVerifiedEvent payload = mapToEmailVerifiedEvent(event);
            publisher.publish(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private KeycloakEmailVerifiedEvent mapToEmailVerifiedEvent(Event event) {
        String email = null;
        Map<String, String> details = event.getDetails();
        if (details != null) {
            email = details.get("email");
        }

        return new KeycloakEmailVerifiedEvent(
                event.getUserId(),
                email,
                Instant.ofEpochMilli(event.getTime())
        );
    }


    @Override
    public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
        // This listener only handles end-user VERIFY_EMAIL events.
    }

    @Override
    public void close() {
        // Publisher lifecycle is managed by the factory.
    }
}
