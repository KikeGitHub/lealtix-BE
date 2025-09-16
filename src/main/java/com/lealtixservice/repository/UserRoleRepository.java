package com.lealtixservice.repository;

import com.lealtixservice.entity.UserRole;
import com.lealtixservice.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    // Puedes agregar métodos personalizados aquí si lo necesitas
}
