package com.ptit.clone.properties;

public class RabbitConfigProperties {
    private String queueName;
    private String exchange;
    private String routingKey;

    public RabbitConfigProperties() {
    }

    public RabbitConfigProperties(String queueName, String exchange, String routingKey) {
        this.queueName = queueName;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
