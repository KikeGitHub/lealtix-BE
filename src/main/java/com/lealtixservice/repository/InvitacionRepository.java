package com.lealtixservice.repository;

import com.lealtixservice.entity.Invitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    Optional<Invitacion> findByToken(String token);
}

