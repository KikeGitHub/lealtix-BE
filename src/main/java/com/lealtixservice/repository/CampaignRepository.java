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
}
