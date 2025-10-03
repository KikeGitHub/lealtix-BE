package com.lealtixservice.repository;

import com.lealtixservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {


    Optional<Tenant> findByAppUserId(Long userId);

    Optional<Tenant> getBySlug(String slug);
}
