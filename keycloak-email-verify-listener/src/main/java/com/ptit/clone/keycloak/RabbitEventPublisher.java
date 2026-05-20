package com.ptit.clone.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitEventPublisher implements AutoCloseable {
    private final ObjectMapper objectMapper;
    private final String exchange;
    private final String routingKey;

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    public RabbitEventPublisher(RabbitPublisherConfig config) {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        this.exchange = config.exchange();
        this.routingKey = config.routingKey();

        try {
            this.connectionFactory = new ConnectionFactory();
            this.connectionFactory.setHost(config.host());
            this.connectionFactory.setPort(config.port());
            this.connectionFactory.setUsername(config.username());
            this.connectionFactory.setPassword(config.password());
            this.connectionFactory.setVirtualHost(config.virtualHost());

        } catch (Exception exception) {
            throw new IllegalStateException("Cannot create RabbitMQ connection", exception);
        }
    }

    public synchronized void publish(Object eventPayload) throws IOException, TimeoutException {
        this.channel = ensureChannel();
        byte[] body = objectMapper.writeValueAsBytes(eventPayload);
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .contentEncoding(StandardCharsets.UTF_8.name())
                .build();
        channel.basicPublish(exchange, routingKey, props, body);
    }

    private Channel ensureChannel() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            connection = connectionFactory.newConnection("keycloak-email-verify-listener");
        }

        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
        }

        return channel;
    }

    @Override
    public void close() {
        try {
            if (channel != null) channel.close();
        } catch (Exception ignored) {
        }
        try {
            if (connection != null) connection.close();
        } catch (Exception ignored) {
        }
    }
}
