package com.lealtixservice.service.impl;

import com.lealtixservice.entity.EmailLog;
import com.lealtixservice.repository.EmailLogRepository;
import com.lealtixservice.service.EmailLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailLogServiceImpl implements EmailLogService {

    private final EmailLogRepository emailLogRepository;

    public EmailLogServiceImpl(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    @Override
    public EmailLog save(EmailLog emailLog) {
        return emailLogRepository.save(emailLog);
    }

    @Override
    public Optional<EmailLog> findById(Long id) {
        return emailLogRepository.findById(id);
    }

    @Override
    public List<EmailLog> findAll() {
        return emailLogRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        emailLogRepository.deleteById(id);
    }
}


