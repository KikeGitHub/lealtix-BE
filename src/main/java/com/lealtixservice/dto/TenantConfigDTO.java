package com.lealtixservice.dto;

import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigDTO {
    private Long id;
    private Long tenantId;
    private String tipoNegocio;
    private String since;
    private String imgLogo;
    private String story;
    private String vision;
    private List<String> listVision;
    private String bussinesEmail;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
    private String tiktok;
    private List<ScheduleDTO> schedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private String day;
        private String hours;
    }
}

