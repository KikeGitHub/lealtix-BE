package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import com.lealtixservice.enums.CampaignStatus;
import com.lealtixservice.enums.PromoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign", indexes = {
        @Index(name = "idx_campaign_business", columnList = "business_id"),
        @Index(name = "idx_campaign_status", columnList = "status"),
        @Index(name = "idx_campaign_start_date", columnList = "start_date"),
        @Index(name = "idx_campaign_business_draft", columnList = "business_id,is_draft")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private CampaignTemplate template; // nullable

    @NotNull
    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @NotBlank
    @Column(length = 200)
    private String title;

    @Column(length = 200)
    private String subtitle;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "promo_type", length = 50)
    private PromoType promoType;

    @Column(name = "promo_value", length = 200)
    private String promoValue; // porcentaje, monto, producto gratis (según tipo)

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CampaignStatus status; // draft, active, inactive, scheduled

    @Column(name = "call_to_action", length = 200)
    private String callToAction;

    @Column(length = 500)
    private String channels; // email, whatsapp, etc (comma separated)

    @Column(columnDefinition = "TEXT")
    private String segmentation; // JSON string opcional

    @Builder.Default
    @NotNull
    @Column(name = "is_automatic")
    private Boolean isAutomatic = false;

    @Builder.Default
    @NotNull
    @Column(name = "is_draft")
    private Boolean isDraft = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "campaign", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CampaignResult result;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Método personalizado para sincronizar status e isDraft
    public void setStatus(CampaignStatus status) {
        this.status = status;
        if (status != null) {
            this.isDraft = (status == CampaignStatus.DRAFT);
        }
    }

    // Método de conveniencia para setear el resultado y mantener consistencia bidireccional
    public void setResultBidirectional(CampaignResult result) {
        this.result = result;
        if (result != null) {
            result.setCampaign(this);
        }
    }
}
