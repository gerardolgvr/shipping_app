package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.domain.PackageType;
import dev.gerardo.shippingapp.service.PackageTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = PackageTypeController.class)
@ActiveProfiles("test")
public class PackageTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PackageTypeService packageTypeService;

    @Test
    public void testTypeEndpoint() throws Exception {
        // Given:
        List<PackageType> packageTypesList = new LinkedList<>();
        packageTypesList.add(new PackageType(1, "Envelop", 2f));
        packageTypesList.add(new PackageType(2, "Box", 2f));
        when(packageTypeService.getPackageTypes()).thenReturn(packageTypesList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/type"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        ObjectMapper mapper = new ObjectMapper();
        List<String> types = mapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {
        });
        assertThat(types).contains("Envelop", "Box");

    }

    @Test
    public void testTypeEndpointWithEmptyResult() throws Exception {
        // Given:
        List<PackageType> packageTypesList = new LinkedList<>();
        when(packageTypeService.getPackageTypes()).thenReturn(packageTypesList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/type"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        ObjectMapper mapper = new ObjectMapper();
        List<String> types = mapper.readValue(response.getContentAsString(), new TypeReference<List<String>>() {
        });
        assertThat(types).isEmpty();
        verify(packageTypeService).getPackageTypes();
        verifyNoMoreInteractions(packageTypeService);
    }

}
