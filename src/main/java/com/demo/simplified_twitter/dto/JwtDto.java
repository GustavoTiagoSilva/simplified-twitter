package com.demo.simplified_twitter.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

public record JwtDto(String accessToken, Long expiresIn) {
    public boolean isLoginCorrect(PasswordEncoder passwordEncoder, LoginRequestDto loginRequest, String password) {
        return passwordEncoder.matches(loginRequest.password(), password);
    }
}
