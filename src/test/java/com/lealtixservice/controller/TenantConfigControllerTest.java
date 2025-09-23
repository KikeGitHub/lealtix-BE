package com.lealtixservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lealtixservice.dto.TenantConfigDTO;
import com.lealtixservice.service.TenantConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @WithMockUser
    void getTenantConfigsByTenant() throws Exception {
        TenantConfigDTO dto1 = TenantConfigDTO.builder().id(1L).tenantId(2L).build();
        TenantConfigDTO dto2 = TenantConfigDTO.builder().id(2L).tenantId(2L).build();
        Mockito.when(tenantConfigService.getTenantConfigsByTenantId(2L)).thenReturn(List.of(dto1, dto2));
        mockMvc.perform(get("/tenant-config/tenant/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

}
