package com.lealtixservice.repository;

import com.lealtixservice.entity.PreRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PreRegistroRepository extends JpaRepository<PreRegistro, Long> {
    Optional<PreRegistro> findByEmail(String email);
}

