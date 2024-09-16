package com.example.spring_authentication.auth;

public record AuthResponseDto(String user, String authToken, String refreshToken) {
}
