package com.lealtixservice.service.impl;

import com.lealtixservice.entity.TenantMenuProduct;
import com.lealtixservice.repository.TenantMenuProductRepository;
import com.lealtixservice.service.TenantMenuProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantMenuProductServiceImpl implements TenantMenuProductService {

    private final TenantMenuProductRepository productRepository;

    @Autowired
    public TenantMenuProductServiceImpl(TenantMenuProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public TenantMenuProduct save(TenantMenuProduct product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<TenantMenuProduct> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<TenantMenuProduct> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}

