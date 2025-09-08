package com.lealtixservice.repository;

import com.lealtixservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String tenantAdmin);
}

