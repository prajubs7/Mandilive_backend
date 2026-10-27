package com.mandilive.mandilive.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        UserSummary user) {
}
