package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.TransportType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransportTypeService {

    private static final Logger logger = LoggerFactory.getLogger(TransportTypeService.class);

    private final RabbitData rabbitData;
    private final ObjectMapper mapper;

    public TransportTypeService(RabbitData rabbitData, ObjectMapper mapper) {
        this.rabbitData = rabbitData;
        this.mapper = mapper;
    }

    public List<String> getTransportTypes() throws JsonProcessingException {
        Optional<Object> response = rabbitData.getData(RabbitMQConstants.TRANSPORT_TYPE_REQUEST);

        if (response.isEmpty()) {
            logger.error("An error ocurred trying to parse transport types");
            throw new UnavailableServiceException("Error fetching data");
        }
        List<TransportType> transportTypeList = parseToTransportTypes(response.get().toString());
        return transportTypeList.stream().map(TransportType::getDescription).collect(Collectors.toList());
    }

    public List<TransportType> parseToTransportTypes(String json) throws JsonProcessingException {
        return Arrays.asList(mapper.readValue(json, TransportType[].class));
    }

}
