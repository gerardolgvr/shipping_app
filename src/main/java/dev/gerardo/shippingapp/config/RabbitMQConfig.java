package dev.gerardo.shippingapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final RabbitMQConfigProperties rabbitMQConfigProperties;

    public RabbitMQConfig(RabbitMQConfigProperties rabbitMQConfigProperties) {
        this.rabbitMQConfigProperties = rabbitMQConfigProperties;
    }

    @Bean
    Queue queue() {
        return new Queue(rabbitMQConfigProperties.getQueue(), false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(rabbitMQConfigProperties.getExchange());
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(rabbitMQConfigProperties.getRoutingKey());
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
