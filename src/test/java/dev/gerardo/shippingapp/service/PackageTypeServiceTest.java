package dev.gerardo.shippingapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.gerardo.shippingapp.domain.PackageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PackageTypeServiceTest {

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    PackageTypeService packageTypeService;

    @Test
    public void shouldParseStringResponseToPackageTypesList() throws JsonProcessingException {
        String dataToParse = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        List<PackageType> typeList = packageTypeService.parseToPackageTypes(dataToParse);
        assertEquals(2, typeList.size());
        assertEquals("Envelop", typeList.get(0).getDescription());
        assertEquals("Box", typeList.get(1).getDescription());
    }

    @Test
    public void shouldObtainAStringListForUI() throws JsonProcessingException {
        String dataToParse = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        List<PackageType> typeList = packageTypeService.parseToPackageTypes(dataToParse);
        assertEquals(2, typeList.size());
        assertEquals("Envelop", typeList.get(0).getDescription());
        assertEquals("Box", typeList.get(1).getDescription());

        List<String> uiPackageTypes = packageTypeService.getUiPackageTypes(typeList);
        assertEquals("Envelop", uiPackageTypes.get(0));
        assertEquals("Box", uiPackageTypes.get(1));
    }

    @Test
    public void shouldGetPackageTypes() throws JsonProcessingException {
        String dataToParse = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        List<PackageType> typeList = packageTypeService.parseToPackageTypes(dataToParse);

        packageTypeService.setPackageTypes(typeList);

        assertEquals(typeList.size(), packageTypeService.getPackageTypes().size());
    }

    @Test
    public void shouldDataPersistInMemory() throws JsonProcessingException {
        String dataToParse = "[{\"id\":3,\"description\":\"Envelop\",\"price\":5},{\"id\":4,\"description\":\"Box\",\"price\":10}]";
        List<PackageType> typeList = packageTypeService.parseToPackageTypes(dataToParse);

        packageTypeService.setPackageTypes(typeList);

        List<String> uiPackageTypes = packageTypeService.getUiPackageTypes(typeList);
        assertEquals("Envelop", uiPackageTypes.get(0));
        assertEquals("Box", uiPackageTypes.get(1));
    }

}
