package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class PackageTypeService {

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

    public List<PackageType> getPackageTypes() {
        List<PackageType> packageTypesList = new LinkedList<>();
        try {
            String request = "{\"type\":\"packageType\"}";
            String response = String.valueOf(rabbitTemplate.convertSendAndReceive(rabbitMQConfigProperties.getExchange(),
                    rabbitMQConfigProperties.getRoutingKey(),
                    request));

            if (response.equals("null")) {
                throw new UnavailableServiceException("Error fetching data");
            }
            packageTypesList = parseToPackageTypes(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return packageTypesList;
    }

    public List<PackageType> parseToPackageTypes(String json) throws JsonProcessingException {
        return mapper.readValue(json, new TypeReference<List<PackageType>>() {
        });
    }

}

