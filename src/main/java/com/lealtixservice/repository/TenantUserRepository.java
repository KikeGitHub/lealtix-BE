package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.entity.TenantUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantUserRepository extends JpaRepository<TenantUser, TenantUserId> {
}

