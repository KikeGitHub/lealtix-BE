package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantMenuProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantMenuProductRepository extends JpaRepository<TenantMenuProduct, Long> {
}

