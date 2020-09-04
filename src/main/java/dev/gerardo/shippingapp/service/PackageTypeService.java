package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PackageTypeService {

    private static final Logger logger = LoggerFactory.getLogger(PackageTypeService.class);

    private final RabbitData rabbitData;
    private final ObjectMapper mapper;

    public PackageTypeService(RabbitData rabbitData, ObjectMapper mapper) {
        this.rabbitData = rabbitData;
        this.mapper = mapper;
    }

    public List<PackageType> getPackageTypes() throws JsonProcessingException {
        Optional<Object> response = rabbitData.getData(RabbitMQConstants.PACKAGE_TYPE_REQUEST);

        if (response.isEmpty()) {
            logger.error("An error ocurred trying to parse: {}", response);
            throw new UnavailableServiceException("Error fetching data");
        }
        return parseToPackageTypes(response.get().toString());
    }

    public List<PackageType> parseToPackageTypes(String json) throws JsonProcessingException {
        return Arrays.asList(mapper.readValue(json, PackageType[].class));
    }

}

