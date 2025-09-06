package com.lealtixservice.service;

import com.lealtixservice.dto.EmailDTO;

import java.io.IOException;

public interface Emailservice {


    public void sendEmail(String to, String subject, String body) throws IOException;

    public void sendEmailWithTemplate(EmailDTO emailDTO) throws IOException;
}
