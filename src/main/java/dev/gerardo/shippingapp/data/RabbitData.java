package dev.gerardo.shippingapp.data;

import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RabbitData {

    private final AmqpTemplate rabbitTemplate;
    private final RabbitMQConfigProperties rabbitMQConfigProperties;

    public RabbitData(AmqpTemplate rabbitTemplate, RabbitMQConfigProperties rabbitMQConfigProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfigProperties = rabbitMQConfigProperties;
    }

    public Optional<Object> getData(String request){
        return Optional.ofNullable(rabbitTemplate.convertSendAndReceive(rabbitMQConfigProperties.getExchange(),
                rabbitMQConfigProperties.getRoutingKey(),
                request));
    }
}
