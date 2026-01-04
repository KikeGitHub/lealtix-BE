package com.lealtixservice.repository;

import com.lealtixservice.entity.Campaign;
import com.lealtixservice.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByBusinessId(Long businessId);
    List<Campaign> findByBusinessIdAndStatus(Long businessId, CampaignStatus status);
    List<Campaign> findByStatus(CampaignStatus status);
    List<Campaign> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);

    // Métodos para borradores
    List<Campaign> findByBusinessIdAndIsDraftOrderByUpdatedAtDesc(Long businessId, boolean isDraft);
    List<Campaign> findByBusinessIdAndIsDraftFalseOrderByCreatedAtDesc(Long businessId);

    // Precargar template para operaciones donde necesitamos acceder a la relación (evita lazy-loading adicional)
    @Query("select c from Campaign c left join fetch c.template where c.id = :id")
    Optional<Campaign> findByIdWithTemplate(@Param("id") Long id);

    // Nuevo: existencia de campaña activa de bienvenida (solo por template.name)
    // Usa consulta nativa optimizada con subquery para validar campaña activa
    @Query(value = "SELECT EXISTS ( " +
                   "  SELECT 1 FROM campaign c " +
                   "  WHERE c.business_id = :tenantId " +
                   "    AND c.status = 'ACTIVE' " +
                   "    AND c.template_id IN ( " +
                   "      SELECT ct.id FROM campaign_template ct WHERE ct.name = :templateName " +
                   "    ) " +
                   ") AS exists_campaign",
           nativeQuery = true)
    boolean existsActiveWelcomeCampaignForTenant(@Param("tenantId") Long tenantId,
                                                @Param("templateName") String templateName);

    // Nuevo: obtener la primera campaña de bienvenida activa (con template y promotionReward precargados)
    @Query("select distinct c from Campaign c " +
           "left join fetch c.template t " +
           "left join fetch c.promotionReward pr " +
           "where c.businessId = :businessId and c.status = :status " +
           "and (c.endDate is null or c.endDate >= CURRENT_DATE) " +
           "and t.category = :category and t.name = :name")
    List<Campaign> findActiveWelcomeCampaignsForTenant(@Param("businessId") Long businessId,
                                                       @Param("status") CampaignStatus status,
                                                       @Param("category") String category,
                                                       @Param("name") String name);
}
