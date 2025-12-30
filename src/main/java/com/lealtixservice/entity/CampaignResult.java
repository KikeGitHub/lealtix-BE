package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_result", indexes = {
        @Index(name = "idx_campaign_result_campaign", columnList = "campaign_id")
})
@Getter
@Setter
@ToString(exclude = {"campaign"})
@EqualsAndHashCode(exclude = {"campaign"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false, unique = true)
    private Campaign campaign;

    @Builder.Default
    private Integer views = 0;

    @Builder.Default
    private Integer clicks = 0;

    @Builder.Default
    private Integer redemptions = 0;

    private LocalDateTime lastViewAt;
    private LocalDateTime lastClickAt;
    private LocalDateTime lastRedemptionAt;

    // Métodos de actualización de métricas
    public void registerView() {
        this.views = (this.views == null ? 0 : this.views) + 1;
        this.lastViewAt = LocalDateTime.now();
    }

    public void registerClick() {
        this.clicks = (this.clicks == null ? 0 : this.clicks) + 1;
        this.lastClickAt = LocalDateTime.now();
    }

    public void registerRedemption() {
        this.redemptions = (this.redemptions == null ? 0 : this.redemptions) + 1;
        this.lastRedemptionAt = LocalDateTime.now();
    }
}
