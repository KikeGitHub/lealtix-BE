package com.lealtixservice.repository;

import com.lealtixservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    // Puedes agregar métodos personalizados aquí si lo necesitas
}

