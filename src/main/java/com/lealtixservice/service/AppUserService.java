package com.lealtixservice.service;

import com.lealtixservice.dto.AppUserDTO;
import com.lealtixservice.dto.TenantUserDTO;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.TenantUser;

import java.util.List;
import java.util.Optional;

public interface AppUserService {
    AppUser save(AppUser user);
    Optional<AppUser> findById(Long id);
    List<AppUser> findAll();
    void deleteById(Long id);
    Optional<AppUser> findByEmail(String email);

    AppUser updateUser(Long id, AppUserDTO user);

    AppUserDTO getUserByToken(String token);

    TenantUserDTO getTenantUserByToken(String token);
}

