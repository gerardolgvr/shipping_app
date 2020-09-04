package dev.gerardo.shippingapp.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.domain.PackageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RabbitDataTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private AmqpTemplate rabbitTemplate;
    @Mock
    private RabbitMQConfigProperties rabbitMQConfigProperties;

    @InjectMocks
    private RabbitData rabbitData;

    @Test
    public void shouldGetData() throws JsonProcessingException {

        // Given:
        String json = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        when(rabbitTemplate.convertSendAndReceive(rabbitMQConfigProperties.getExchange(),
                rabbitMQConfigProperties.getRoutingKey(),
                RabbitMQConstants.PACKAGE_TYPE_REQUEST)
        ).thenReturn(json);
        PackageType[] packageTypesList = new PackageType[]{
                new PackageType(3, "Envelop", 5f),
                new PackageType(4, "Box", 10f)
        };
        when(mapper.readValue(eq(json), eq(PackageType[].class))).thenReturn(packageTypesList);

        // When:
        String response = rabbitData.getData(RabbitMQConstants.PACKAGE_TYPE_REQUEST).get().toString();
        List<PackageType> expectedPackageTypeList = Arrays.asList(mapper.readValue(response, PackageType[].class));

        // Then:
        assertThat(expectedPackageTypeList.size()).isEqualTo(2);
        assertThat(expectedPackageTypeList.get(0).getDescription()).isEqualTo("Envelop");
        assertThat(expectedPackageTypeList.get(1).getDescription()).isEqualTo("Box");

    }

}
