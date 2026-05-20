package com.ptit.clone.keycloak;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KeycloakVerifyEmailEventListenerProviderFactory implements EventListenerProviderFactory {

    private RabbitEventPublisher publisher;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new KeycloakVerifyEmailEventListenerProvider(publisher);
    }

    @Override
    public void init(Config.Scope config) {
        publisher = new RabbitEventPublisher(RabbitPublisherConfig.from(config));
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
        if (publisher != null) {
            publisher.close();
        }
    }

    @Override
    public String getId() {
        return "keycloak-to-rabbitmq";
    }
}
