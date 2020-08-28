package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageTypeService {

    private static final Logger logger = LoggerFactory.getLogger(PackageTypeService.class);

    private final AmqpTemplate rabbitTemplate;
    private final RabbitMQConfigProperties rabbitMQConfigProperties;
    private final ObjectMapper mapper;

    public PackageTypeService(AmqpTemplate rabbitTemplate,
                              RabbitMQConfigProperties rabbitMQConfigProperties,
                              ObjectMapper mapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfigProperties = rabbitMQConfigProperties;
        this.mapper = mapper;
    }

    public List<PackageType> getPackageTypes() throws JsonProcessingException {
        String request = "{\"type\":\"packageType\"}";
        String response = String.valueOf(rabbitTemplate.convertSendAndReceive(rabbitMQConfigProperties.getExchange(),
                rabbitMQConfigProperties.getRoutingKey(),
                request));

        if (response.equals("null")) {
            logger.error("An error ocurred trying to parse: {}", response);
            throw new UnavailableServiceException("Error fetching data");
        }
        return parseToPackageTypes(response);
    }

    public List<PackageType> parseToPackageTypes(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<PackageType>>() {
        });
    }

}

