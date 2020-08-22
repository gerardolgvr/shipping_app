package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PackageTypeService {

    private List<PackageType> packageTypes;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${shippingapp.rabbitmq.exchange}")
    private String exchange;

    @Value("${shippingapp.rabbitmq.routingkey}")
    private String routingKey;

    public List<PackageType> requestAndReceive() throws JsonProcessingException {
        String request = "{\"type\":\"packageType\"}";
        String response = String.valueOf(rabbitTemplate.convertSendAndReceive(exchange, routingKey, request));
        if (response.equals("null")) {
            throw new UnavailableServiceException("Error fetching data");
        }
        packageTypes = parseToPackageTypes(response);
        return packageTypes;
    }

    public List<String> getUiPackageTypes(List<PackageType> typesList) {
        return typesList.stream().map(PackageType::getDescription).collect(Collectors.toList());
    }

    public List<PackageType> parseToPackageTypes(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<PackageType> types = mapper.readValue(json, new TypeReference<List<PackageType>>() {
        });
        return types;
    }

    public List<PackageType> getPackageTypes() {
        return packageTypes;
    }

    public void setPackageTypes(List<PackageType> packageTypes) {
        this.packageTypes = packageTypes;
    }
}

