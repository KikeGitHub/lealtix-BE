package com.lealtixservice.service.impl;

import com.lealtixservice.dto.AppUserDTO;
import com.lealtixservice.dto.TenantUserDTO;
import com.lealtixservice.entity.AppUser;
import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.repository.AppUserRepository;
import com.lealtixservice.repository.TenantUserRepository;
import com.lealtixservice.service.AppUserService;
import com.lealtixservice.service.TokenService;
import com.lealtixservice.util.EncrypUtils;
import com.lealtixservice.util.TenantUserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TenantUserRepository tenantUserRepository;

    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public List<AppUser> findAll() {
        return appUserRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return Optional.ofNullable(appUserRepository.findByEmail(email));
    }

    @Override
    public AppUser updateUser(Long id, AppUserDTO user) {
        AppUser existingUser = appUserRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found with id " + id)
        );
        existingUser.setFullName(user.getFullName());
        existingUser.setFechaNacimiento(user.getFechaNacimiento());
        existingUser.setTelefono(user.getTelefono());
        existingUser.setEmail(user.getEmail());
        existingUser.setPasswordHash(EncrypUtils.encryptPassword(user.getPassword()));
        existingUser.setUpdatedAt(LocalDateTime.now());
        return appUserRepository.save(existingUser);
    }

    @Override
    public AppUserDTO getUserByToken(String token) {
        Jws<Claims> claims = tokenService.validateToken(token);
        if (claims != null) {
            String email = claims.getBody().get("email", String.class);
            AppUser user = findByEmail(email).orElseThrow(
                    () -> new RuntimeException("User not found with email " + email)
            );
            return AppUserDTO.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .fechaNacimiento(user.getFechaNacimiento())
                    .telefono(user.getTelefono())
                    .email(user.getEmail())
                    .password(EncrypUtils.decrypPassword(user.getPasswordHash()))
                    .build();
        }
        return null;
    }

    @Override
    public TenantUserDTO getTenantUserByToken(String token) {
        Jws<Claims> claims = tokenService.validateToken(token);
        if (claims != null) {
            String email = claims.getBody().get("email", String.class);
            AppUser user = findByEmail(email).orElseThrow(
                    () -> new RuntimeException("User not found with email " + email)
            );
            TenantUser tenUser = tenantUserRepository.findByUserId(user.getId());
            if (tenUser == null) {
                return null;
            } else {
                return TenantUserMapper.toDTO(tenUser);
            }
        }
        return null;
    }
}

