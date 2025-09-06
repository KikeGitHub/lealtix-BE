package com.lealtixservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SendGridTemplates {

    @Value("${sendgrid.templates.pre-registro}")
    private String preRegistroTemplate;

    public String getPreRegistroTemplate() {
        return preRegistroTemplate;
    }
}

