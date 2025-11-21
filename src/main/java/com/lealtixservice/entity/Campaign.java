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
        @Index(name = "idx_campaign_business", columnList = "businessId"),
        @Index(name = "idx_campaign_status", columnList = "status"),
        @Index(name = "idx_campaign_start_date", columnList = "startDate")
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
    @Column(nullable = false)
    private Long businessId;

    @NotBlank
    @Column(length = 200)
    private String title;

    @Column(length = 200)
    private String subtitle;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PromoType promoType;

    @Column(length = 200)
    private String promoValue; // porcentaje, monto, producto gratis (según tipo)

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private CampaignStatus status; // draft, active, inactive, scheduled

    @Column(length = 200)
    private String callToAction;

    @Column(length = 500)
    private String channels; // email, whatsapp, etc (comma separated)

    @Column(columnDefinition = "TEXT")
    private String segmentation; // JSON string opcional

    @Builder.Default
    @NotNull
    private Boolean isAutomatic = false;

    private LocalDateTime createdAt;
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

    // Método de conveniencia para setear el resultado y mantener consistencia bidireccional
    public void setResultBidirectional(CampaignResult result) {
        this.result = result;
        if (result != null) {
            result.setCampaign(this);
        }
    }
}
