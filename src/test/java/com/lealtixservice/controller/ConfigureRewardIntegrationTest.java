package com.lealtixservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lealtixservice.dto.ConfigureRewardRequest;
import com.lealtixservice.dto.PromotionRewardResponse;
import com.lealtixservice.enums.RewardType;
import com.lealtixservice.service.CampaignService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración para CampaignController - endpoint de configuración de rewards.
 * Usa @WebMvcTest para test slice del controller sin cargar todo el ApplicationContext.
 * Excluye SecurityAutoConfiguration para evitar problemas con autenticación en tests.
 */
@WebMvcTest(
    controllers = CampaignController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class ConfigureRewardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CampaignService campaignService;

    @Test
    public void postConfigureReward_ShouldReturnDescriptionInResponse() throws Exception {
        ConfigureRewardRequest req = ConfigureRewardRequest.builder()
                .rewardType(RewardType.PERCENT_DISCOUNT)
                .description("Prueba descripción guardada")
                .numericValue(BigDecimal.valueOf(10))
                .minPurchaseAmount(BigDecimal.ZERO)
                .usageLimit(1000)
                .build();

        PromotionRewardResponse resp = PromotionRewardResponse.builder()
                .id(1L)
                .campaignId(1L)
                .rewardType(RewardType.PERCENT_DISCOUNT)
                .description("Prueba descripción guardada")
                .numericValue(BigDecimal.valueOf(10))
                .usageLimit(1000)
                .build();

        when(campaignService.configureReward(anyLong(), any(ConfigureRewardRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/campaigns/123/reward")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.object.description").value("Prueba descripción guardada"));
    }
}
