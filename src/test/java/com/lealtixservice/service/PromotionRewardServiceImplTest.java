package com.lealtixservice.service;

import com.lealtixservice.dto.PromotionRewardDTO;
import com.lealtixservice.entity.Campaign;
import com.lealtixservice.entity.PromotionReward;
import com.lealtixservice.repository.PromotionRewardRepository;
import com.lealtixservice.service.impl.PromotionRewardServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PromotionRewardServiceImplTest {

    @Mock
    private PromotionRewardRepository promotionRewardRepository;

    private PromotionRewardServiceImpl service;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        service = new PromotionRewardServiceImpl(promotionRewardRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void update_ShouldPersistDescription_WhenValid() {
        // prepare existing reward with campaign to avoid NPE in mapToResponse
        Campaign camp = Campaign.builder().id(10L).build();
        PromotionReward existing = PromotionReward.builder().id(1L).campaign(camp).build();
        when(promotionRewardRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(promotionRewardRepository.save(any(PromotionReward.class))).thenAnswer(i -> i.getArgument(0));

        PromotionRewardDTO dto = PromotionRewardDTO.builder()
                .rewardType(com.lealtixservice.enums.RewardType.CUSTOM)
                .description("Texto de prueba")
                .numericValue(BigDecimal.valueOf(10)).build();

        service.update(1L, dto);

        ArgumentCaptor<PromotionReward> captor = ArgumentCaptor.forClass(PromotionReward.class);
        verify(promotionRewardRepository).save(captor.capture());
        PromotionReward saved = captor.getValue();
        assertThat(saved.getDescription()).isEqualTo("Texto de prueba");
    }

    @Test
    void update_ShouldThrow_WhenDescriptionTooLong() {
        Campaign camp = Campaign.builder().id(20L).build();
        PromotionReward existing = PromotionReward.builder().id(2L).campaign(camp).build();
        when(promotionRewardRepository.findById(2L)).thenReturn(Optional.of(existing));

        String longDesc = "x".repeat(600);
        PromotionRewardDTO dto = PromotionRewardDTO.builder().rewardType(null).description(longDesc).build();
        // Use CUSTOM to bypass specific type validations (we just want to test description length)
        dto.setRewardType(com.lealtixservice.enums.RewardType.CUSTOM);

        assertThrows(RuntimeException.class, () -> service.update(2L, dto));
    }
}
