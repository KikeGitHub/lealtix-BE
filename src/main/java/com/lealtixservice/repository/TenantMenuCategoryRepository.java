package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantMenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantMenuCategoryRepository extends JpaRepository<TenantMenuCategory, Long> {
}

