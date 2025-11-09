package com.lealtixservice.service;

import com.lealtixservice.entity.EmailLog;
import java.util.List;
import java.util.Optional;

public interface EmailLogService {
    EmailLog save(EmailLog emailLog);
    Optional<EmailLog> findById(Long id);
    List<EmailLog> findAll();
    void deleteById(Long id);
}

