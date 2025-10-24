package com.lealtixservice.dto;

public class JwtResponse {
    private String accessToken;
    private String userEmail;
    private Long userId;

    public JwtResponse(String accessToken, String userEmail, Long userId) {
        this.accessToken = accessToken;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    public String getAccessToken() { return accessToken; }
    public String getUserEmail() { return userEmail; }
    public Long getUserId() { return userId; }
}

