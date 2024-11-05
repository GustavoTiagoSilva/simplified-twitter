package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.JwtDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final String applicationName;
    private final JwtEncoder jwtEncoder;
    private final String expiresIn;

    public TokenService(@Value("${spring.application.name}") String applicationName,
                        @Value("${jwt.token.expires-in}") String expiresIn,
                        JwtEncoder jwtEncoder) {
        this.applicationName = applicationName;
        this.jwtEncoder = jwtEncoder;
        this.expiresIn = expiresIn;
    }

    public JwtDto getJwtForUser(UserDto user) {
        long expiresIn = Long.parseLong(this.expiresIn);
        var scopes = user.roles().stream().map(RoleDto::name).collect(Collectors.joining(" "));
        var claims = JwtClaimsSet.builder()
                .issuer(this.applicationName)
                .subject(user.id().toString())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .issuedAt(Instant.now())
                .claim("scope", scopes)
                .build();
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new JwtDto(jwtValue, expiresIn);
    }

}
