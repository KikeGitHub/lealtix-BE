package com.lealtixservice.entity;


import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
@Entity
@Table(name = "email_log")
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "template_name", nullable = false, length = 100)
    private String templateName;

    @Column(name = "sendgrid_message_id", length = 255)
    private String sendgridMessageId;

    @Column(nullable = false, length = 50)
    private String status = "pending";

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public EmailLog() {}

    // Constructor para @Builder
    public EmailLog(Long id, String entityType, Long entityId, String email, String templateName, String sendgridMessageId, String status, String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.email = email;
        this.templateName = templateName;
        this.sendgridMessageId = sendgridMessageId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getSendgridMessageId() { return sendgridMessageId; }
    public void setSendgridMessageId(String sendgridMessageId) { this.sendgridMessageId = sendgridMessageId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
