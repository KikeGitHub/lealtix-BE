package com.lealtixservice.entity;

import jakarta.persistence.*;
import lombok.*;
import com.lealtixservice.enums.PromoType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campaign_template", indexes = {
        @Index(name = "idx_campaign_template_category", columnList = "category"),
        @Index(name = "idx_campaign_template_is_active", columnList = "isActive")
})
@Getter
@Setter
@ToString(exclude = {"campaigns"})
@EqualsAndHashCode(exclude = {"campaigns"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 100)
    private String category; // cumpleaños, bienvenida, navidad

    @Column(length = 200)
    private String defaultTitle;

    @Column(length = 200)
    private String defaultSubtitle;

    @Column(length = 1000)
    private String defaultDescription;

    @Column(length = 500)
    private String defaultImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PromoType defaultPromoType; // discount, amount, bogo, freeItem, custom

    @Builder.Default
    @NotNull
    private Boolean isActive = true;

    // Relación con Campaign (opcional listar campañas que usan este template)
    @Builder.Default
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<com.lealtixservice.entity.Campaign> campaigns = new ArrayList<>();
}
