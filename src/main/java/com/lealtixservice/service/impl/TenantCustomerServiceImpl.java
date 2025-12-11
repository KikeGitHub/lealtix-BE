package com.lealtixservice.service.impl;

import com.lealtixservice.entity.TenantCustomer;
import com.lealtixservice.repository.TenantCustomerRepository;
import com.lealtixservice.service.TenantCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantCustomerServiceImpl implements TenantCustomerService {

    @Autowired
    private TenantCustomerRepository tenantCustomerRepository;

    @Override
    public TenantCustomer save(TenantCustomer customer) {
        return tenantCustomerRepository.save(customer);
    }

    @Override
    public Optional<TenantCustomer> findById(Long id) {
        return tenantCustomerRepository.findById(id);
    }

    @Override
    public List<TenantCustomer> findAll() {
        return tenantCustomerRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tenantCustomerRepository.deleteById(id);
    }

    @Override
    public List<TenantCustomer> findByTenantId(Long tenantId) {
        return tenantCustomerRepository.findByTenantId(tenantId);
    }
}

