package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantMenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantMenuCategoryRepository extends JpaRepository<TenantMenuCategory, Long> {
    List<TenantMenuCategory> findByTenantId(Long tenantId);

    List<TenantMenuCategory> findByTenantIdOrderByDisplayOrderAsc(Long tenantId);

    List<TenantMenuCategory> findByTenantIdAndIsActiveTrueOrderByDisplayOrderAsc(Long tenantId);

    @Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM TenantMenuCategory c WHERE c.tenant.id = :tenantId")
    Integer findMaxDisplayOrderByTenantId(@Param("tenantId") Long tenantId);
}

