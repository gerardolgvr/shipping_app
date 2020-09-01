package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PackageTypeServiceTest {

    @InjectMocks
    PackageTypeService packageTypeService;
    @Mock
    private RabbitData rabbitData;
    @Mock
    private RabbitMQConfigProperties rabbitMQConfigProperties;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void shouldGetPackageTypes() throws JsonProcessingException {

        // Given:
        String json = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        when(rabbitData.getData(RabbitMQConstants.PACKAGE_TYPE_REQUEST)).thenReturn(json);
        PackageType[] packageTypesList = new PackageType[]{
                new PackageType(3, "Envelop", 5f),
                new PackageType(4, "Box", 10f)
        };
        when(objectMapper.readValue(eq(json), eq(PackageType[].class))).thenReturn(packageTypesList);

        // When:
        List<PackageType> packageTypes = packageTypeService.getPackageTypes();

        // Then:
        assertThat(packageTypes.get(0).getId()).isEqualTo(3);
        assertThat(packageTypes.get(0).getDescription()).isEqualTo("Envelop");
        assertThat(packageTypes.get(1).getId()).isEqualTo(4);
        assertThat(packageTypes.get(1).getDescription()).isEqualTo("Box");

    }

    @Test
    public void shouldParseJsonToPackageTypes() throws JsonProcessingException {

        // Given:
        PackageType[] packageTypesList = new PackageType[]{
                new PackageType(3, "Envelop", 5f),
                new PackageType(4, "Box", 10f)
        };
        String json = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        when(objectMapper.readValue(eq(json), eq(PackageType[].class))).thenReturn(packageTypesList);

        // When:
        List<PackageType> packageTypes = packageTypeService.parseToPackageTypes(json);

        // Then:
        assertThat(packageTypesList[0].getDescription()).isEqualTo("Envelop");
        assertThat(packageTypesList[1].getDescription()).isEqualTo("Box");

    }

    @Test
    public void testServiceWhenDataIsNotAvailable() {

        // Given:
        String json = "null";
        when(rabbitData.getData(RabbitMQConstants.PACKAGE_TYPE_REQUEST)).thenReturn(json);

        // When:
        Throwable thrown = catchThrowable(() -> {
            List<PackageType> packageTypes = packageTypeService.getPackageTypes();
        });

        // Then:
        assertThat(thrown)
                .isInstanceOf(UnavailableServiceException.class)
                .hasMessage("Error fetching data");
    }

}
