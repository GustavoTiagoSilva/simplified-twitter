package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.LoginRequestDto;
import com.demo.simplified_twitter.dto.LoginResponseDto;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginService {

    private final UserService userService;
    private final String applicationName;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserService userService,
                        @Value("${spring.application.name}") String applicationName,
                        JwtEncoder jwtEncoder,
                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.applicationName = applicationName;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        var user = userService.findByUsername(loginRequest.username());
        if (!this.isLoginCorrect(loginRequest, user.password())) {
            throw new BadCredentialsException("User or password is invalid");
        }
        long expiresIn = 300L;
        var claims = JwtClaimsSet.builder()
                .issuer(this.applicationName)
                .subject(user.id().toString())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .build();
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new LoginResponseDto(jwtValue, expiresIn);
    }

    public boolean isLoginCorrect(LoginRequestDto loginRequest, String password) {
        return passwordEncoder.matches(loginRequest.password(), password);
    }
}
