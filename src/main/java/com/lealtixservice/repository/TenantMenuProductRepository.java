package com.lealtixservice.repository;

import com.lealtixservice.entity.TenantMenuProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantMenuProductRepository extends JpaRepository<TenantMenuProduct, Long> {
    List<TenantMenuProduct> findByCategoryId(Long id);
}

