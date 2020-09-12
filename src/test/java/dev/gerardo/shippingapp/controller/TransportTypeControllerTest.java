package dev.gerardo.shippingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gerardo.shippingapp.exception.UnavailableServiceException;
import dev.gerardo.shippingapp.service.TransportTypeService;
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

@WebMvcTest(controllers = TransportTypeController.class)
@ActiveProfiles("test")
public class TransportTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TransportTypeService transportTypeService;

    @Test
    public void testTransportTypeEndpoint() throws Exception {

        // Given:
        List<String> transportTypesList = new LinkedList<>();
        transportTypesList.add("Land");
        transportTypesList.add("Air");
        when(transportTypeService.getTransportTypes()).thenReturn(transportTypesList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/transport/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<String> transportTypes = Arrays.asList(mapper.readValue(response.getContentAsString(), String[].class));
        assertThat(transportTypes).isEqualTo(transportTypesList);

    }

    @Test
    public void testTransportTypeEndpointWithEmptyResults() throws Exception {

        // Given:
        List<String> transportTypesList = new LinkedList<>();
        when(transportTypeService.getTransportTypes()).thenReturn(transportTypesList);

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/transport/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List<String> transportTypes = Arrays.asList(mapper.readValue(response.getContentAsString(), String[].class));
        assertThat(transportTypes).isEmpty();
        verify(transportTypeService).getTransportTypes();
        verifyNoMoreInteractions(transportTypeService);

    }

    @Test
    public void testTransportTypeEndpointWhenServiceIsUnavailable() throws Exception {

        // Given:
        when(transportTypeService.getTransportTypes()).thenThrow(new UnavailableServiceException("Error fetching data"));

        // When:
        MockHttpServletResponse response = mockMvc.perform(get("/transport/Medium"))
                .andReturn().getResponse();

        // Then:
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getErrorMessage()).isEqualTo("Error fetching data");

    }
}
