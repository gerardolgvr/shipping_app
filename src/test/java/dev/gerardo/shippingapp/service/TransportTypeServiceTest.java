package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.TransportType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransportTypeServiceTest {

    @Mock
    private RabbitData rabbitData;

    @Mock
    private RabbitMQConfigProperties rabbitMQConfigProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransportTypeService transportTypeService;

    private final String json = "[{\"id\":3,\"description\":\"Land\",\"pricePerMile\":2},{\"id\":4,\"description\":\"Air\",\"pricePerMile\":3}]";

    @Test
    public void shouldGetTransportTypes() throws JsonProcessingException {

        // Given:
        when(rabbitData.getData(RabbitMQConstants.TRANSPORT_TYPE_REQUEST)).thenReturn(Optional.of(json));
        TransportType[] transportTypesList = TransportTypeServiceTest.getTransportTypes();
        when(objectMapper.readValue(eq(json), eq(TransportType[].class))).thenReturn(transportTypesList);

        // When:
        List<String> transportTypes = transportTypeService.getTransportTypes();

        // Then:
        List<String> expectedTransportTypes = Arrays.asList(transportTypesList)
                .stream()
                .map(TransportType::getDescription)
                .collect(Collectors.toList());
        assertThat(transportTypes).isEqualTo(expectedTransportTypes);

    }

    @Test
    public void shouldParseJsonToTransportTypes() throws JsonProcessingException {

        // Given:
        TransportType[] transportTypesList = TransportTypeServiceTest.getTransportTypes();
        when(objectMapper.readValue(eq(json), eq(TransportType[].class))).thenReturn(transportTypesList);

        // When:
        List<TransportType> transportTypes = transportTypeService.parseToTransportTypes(json);

        // Then:
        assertThat(transportTypes.get(0)).isEqualTo(transportTypesList[0]);
        assertThat(transportTypes.get(1)).isEqualTo(transportTypesList[1]);

    }

    @Test
    public void testServiceWhenDataIsNotAvailable() {

        // Given:
        String json = null;
        when(rabbitData.getData(RabbitMQConstants.TRANSPORT_TYPE_REQUEST)).thenReturn(Optional.ofNullable(json));

        // When:
        Throwable thrown = catchThrowable(() -> {
            List<String> transportTypes = transportTypeService.getTransportTypes();
        });

        // Then:
        assertThat(thrown)
                .isInstanceOf(UnavailableServiceException.class)
                .hasMessage("Error fetching data");

    }

    private static TransportType[]  getTransportTypes() {
        TransportType[] transportTypesList = new TransportType[]{
                new TransportType(3, "Land", 2),
                new TransportType(4, "Air", 3)
        };
        return transportTypesList;
    }
}
