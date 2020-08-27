package dev.gerardo.shippingapp.controller;

import dev.gerardo.shippingapp.service.PackageTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest( controllers = PackageTypeController.class)
@ActiveProfiles("test")
public class PackageTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PackageTypeService packageTypeService;

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello")));
    }

    @Test
    public void testTypeEndpoint() throws Exception {
        mockMvc.perform(get("/type"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Box")));
    }
}
