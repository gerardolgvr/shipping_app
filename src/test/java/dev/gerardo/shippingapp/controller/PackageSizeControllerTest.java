package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.domain.PackageSize;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.PackageSizeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = PackageSizeController.class)
@ActiveProfiles("test")
public class PackageSizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PackageSizeService packageSizeService;

    @Test
    public void testPackageSizeEndpoint() throws Exception {

        // Given:
        List<PackageSize> packageSizeList = new LinkedList<>();
        packageSizeList.add(new PackageSize(4, "Small", 10f));
        packageSizeList.add(new PackageSize(5, "Medium", 25f));
        packageSizeList.add(new PackageSize(6, "Large", 50f));
        when(packageSizeService.getPackageSizes()).thenReturn(packageSizeList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/size/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<String> packageSizes = Arrays.asList(mapper.readValue(response.getContentAsString(), String[].class));
        assertThat(packageSizes).contains("Small", "Medium", "Large");

    }

    @Test
    public void testPackageSizeEndpointWithEmptyResults() throws Exception {

        // Given:
        List<PackageSize> packageSizeList = new LinkedList<>();
        when(packageSizeService.getPackageSizes()).thenReturn(packageSizeList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/size/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<String> packageSizes = Arrays.asList(mapper.readValue(response.getContentAsString(), String[].class));
        assertThat(packageSizes).isEmpty();
        verify(packageSizeService).getPackageSizes();
        verifyNoMoreInteractions(packageSizeService);

    }

    @Test
    public void testPackageSizeEndpointWhenServiceIsUnavailable() throws Exception {

        // Given:
        when(packageSizeService.getPackageSizes()).thenThrow(new UnavailableServiceException("Error fetching data"));

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/size/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).isEqualTo("Error fetching data");

    }

}
