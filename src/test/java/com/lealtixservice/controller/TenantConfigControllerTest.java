package com.lealtixservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lealtixservice.service.TenantConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantConfigController.class)
class TenantConfigControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TenantConfigService tenantConfigService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getTenantConfig_notFound() throws Exception {
        Mockito.when(tenantConfigService.getTenantConfigById(99L)).thenReturn(null);
        mockMvc.perform(get("/tenant-config/99"))
                .andExpect(status().isNotFound());
    }

}
