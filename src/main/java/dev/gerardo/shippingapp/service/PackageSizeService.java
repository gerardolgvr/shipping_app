package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.PackageSize;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PackageSizeService {

    private static final Logger logger = LoggerFactory.getLogger(PackageSizeService.class);

    private final RabbitData rabbitData;
    private final ObjectMapper mapper;


    public PackageSizeService(RabbitData rabbitData, ObjectMapper mapper) {
        this.rabbitData = rabbitData;
        this.mapper = mapper;
    }

    public List<String> getPackageSizes() throws JsonProcessingException {
        Optional<Object> response = rabbitData.getData(RabbitMQConstants.PACKAGE_SIZE_REQUEST);

        if (response.isEmpty()) {
            logger.error("An error ocurred trying to parse package sizes: {}", response);
            throw new UnavailableServiceException("Error fetching data");
        }
        List<PackageSize> packageSizeList = parseToPackageSizes(response.get().toString());
        return packageSizeList.stream().map(PackageSize::getDescription).collect(Collectors.toList());
    }

    public List<PackageSize> parseToPackageSizes(String json) throws JsonProcessingException {
        return Arrays.asList(mapper.readValue(json, PackageSize[].class));
    }

}
