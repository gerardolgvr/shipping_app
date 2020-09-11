package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.config.RabbitMQConfigProperties;
import dev.gerardo.shippingapp.constants.RabbitMQConstants;
import dev.gerardo.shippingapp.data.RabbitData;
import dev.gerardo.shippingapp.domain.PackageSize;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PackageSizeServiceTest {

    @Mock
    private RabbitData rabbitData;

    @Mock
    private RabbitMQConfigProperties rabbitMQConfigProperties;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PackageSizeService packageSizeService;

    private final String json = "[{\"id\":4,\"description\":\"Small\",\"priceFactor\":10},{\"id\":5,\"description\":\"Medium\",\"priceFactor\":25},{\"id\":6,\"description\":\"Large\",\"priceFactor\":50}]";

    @Test
    public void shouldGetPackageSizes() throws JsonProcessingException {

        // Given:
        when(rabbitData.getData(RabbitMQConstants.PACKAGE_SIZE_REQUEST)).thenReturn(Optional.of(json));
        PackageSize[] packageSizesList = new PackageSize[]{
                new PackageSize(4, "Small", 10f),
                new PackageSize(5, "Medium", 25f),
                new PackageSize(6, "Large", 50f)
        };
        when(objectMapper.readValue(eq(json), eq(PackageSize[].class))).thenReturn(packageSizesList);

        // When:
        List<String> packageSizes = packageSizeService.getPackageSizes();

        // Then:
        assertThat(packageSizes.get(0)).isEqualTo("Small");
        assertThat(packageSizes.get(1)).isEqualTo("Medium");
        assertThat(packageSizes.get(2)).isEqualTo("Large");

    }

    @Test
    public void shouldParseJsonToPackageSizes() throws JsonProcessingException {

        // Given:
        PackageSize[] packageSizesList = new PackageSize[]{
                new PackageSize(4, "Small", 10f),
                new PackageSize(5, "Medium", 25f),
                new PackageSize(6, "Large", 50f)
        };
        when(objectMapper.readValue(eq(json), eq(PackageSize[].class))).thenReturn(packageSizesList);

        // When:
        List<PackageSize> packageSizes = packageSizeService.parseToPackageSizes(json);

        // Then:
        assertThat(packageSizes.get(0)).isEqualTo(packageSizesList[0]);
        assertThat(packageSizes.get(1)).isEqualTo(packageSizesList[1]);
        assertThat(packageSizes.get(2)).isEqualTo(packageSizesList[2]);

    }

    @Test
    public void testServiceWhenDataIsNotAvailable() {

        // Given:
        String json = null;
        when(rabbitData.getData(RabbitMQConstants.PACKAGE_SIZE_REQUEST)).thenReturn(Optional.ofNullable(json));

        // When:
        Throwable thrown = catchThrowable(() -> {
            List<String> packageSizes = packageSizeService.getPackageSizes();
        });

        // Then:
        assertThat(thrown)
                .isInstanceOf(UnavailableServiceException.class)
                .hasMessage("Error fetching data");

    }

}
