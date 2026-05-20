package com.ptit.clone;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitmqConsumerConfiguration {
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory (ConnectionFactory connectionFactory, JacksonJsonMessageConverter converter){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
