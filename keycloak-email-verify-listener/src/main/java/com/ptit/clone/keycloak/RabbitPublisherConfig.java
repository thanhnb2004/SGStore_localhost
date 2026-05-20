package com.ptit.clone.keycloak;

import org.keycloak.Config;

public record RabbitPublisherConfig(
        String host,
        int port,
        String username,
        String password,
        String virtualHost,
        String exchange,
        String routingKey
) {

    public static RabbitPublisherConfig from(Config.Scope config) {
        String host = config.get("host");
        int port = Integer.parseInt(config.get("port"));
        String username = config.get("username");
        String password = config.get("password");
        String virtualHost = config.get("virtual-host");
        String exchange = config.get("exchange");
        String routingKey = config.get("routing-key");

        return new RabbitPublisherConfig(host, port, username, password, virtualHost, exchange, routingKey);
    }


}
