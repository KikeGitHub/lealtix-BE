package com.lealtixservice.service.impl;

import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.entity.TenantUserId;
import com.lealtixservice.repository.AppUserRepository;
import com.lealtixservice.repository.TenantUserRepository;
import com.lealtixservice.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TenantUserServiceImpl implements TenantUserService {

    @Autowired
    private TenantUserRepository tenantUserRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public Optional<TenantUser> findById(TenantUserId id) {
        return tenantUserRepository.findById(id);
    }

    @Override
    public Optional<TenantUser> findByUserId(Long userId) {
        return Optional.ofNullable(tenantUserRepository.findByUserId(userId));
    }

    @Override
    public TenantUser save(TenantUser tenantUser) {
        return tenantUserRepository.save(tenantUser);
    }

    @Override
    public void deleteById(TenantUserId id) {
        tenantUserRepository.deleteById(id);
    }

    @Override
    public Optional<TenantUser> findByEmail(String email) {
        var user = appUserRepository.findByEmail(email);
        if (user != null) {
            TenantUser tenUser = tenantUserRepository.findByUserId(user.getId());
            return Optional.of(tenUser);
        }
        return Optional.empty();
    }
}

