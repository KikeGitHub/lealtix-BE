package com.lealtixservice.service;

import com.lealtixservice.entity.TenantUser;
import com.lealtixservice.entity.TenantUserId;
import java.util.Optional;

/**
 * Servicio para operaciones sobre TenantUser.
 */
public interface TenantUserService {
    /**
     * Busca un TenantUser por su ID compuesto.
     * @param id ID compuesto de TenantUser
     * @return Optional con TenantUser si existe
     */
    Optional<TenantUser> findById(TenantUserId id);

    /**
     * Busca un TenantUser por el userId.
     * @param userId ID del usuario
     * @return Optional con TenantUser si existe
     */
    Optional<TenantUser> findByUserId(Long userId);

    /**
     * Guarda o actualiza un TenantUser.
     * @param tenantUser entidad a guardar
     * @return entidad guardada
     */
    TenantUser save(TenantUser tenantUser);

    /**
     * Elimina un TenantUser por su ID compuesto.
     * @param id ID compuesto de TenantUser
     */
    void deleteById(TenantUserId id);

    Optional<TenantUser> findByEmail(String email);
}

