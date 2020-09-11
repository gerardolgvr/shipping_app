package dev.gerardo.shippingapp.constants;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQConstants {

    public static final String PACKAGE_TYPE_REQUEST = "{\"type\":\"packageType\"}";
    public static final String PACKAGE_SIZE_REQUEST = "{\"type\":\"packageSize\"}";

}
