package com.demo.simplified_twitter.service;


import com.demo.simplified_twitter.dto.JwtDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.faker.UserEntityFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceTests {
    private final String applicationName = "simplified-twitter";
    private final Long tokenExpiresIn = 86400L;
    private final JwtEncoder jwtEncoder = mock();
    private final TokenService tokenService = new TokenService(applicationName, tokenExpiresIn, jwtEncoder);

    @Test
    @DisplayName("Should return jwt token when valid user")
    void shouldReturnJwtTokenWhenValidUser() {
        String tokenValue = "jwtToken";
        User userEntity = UserEntityFaker.fakeUserWithId(Role.Values.ADMIN);
        Set<RoleDto> roles = userEntity
                .getRoles()
                .stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
        UserDto user = new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), roles);
        var scopes = user.roles().stream().map(RoleDto::name).collect(Collectors.joining(" "));
        var claims = JwtClaimsSet.builder()
                .issuer(this.applicationName)
                .subject(user.id().toString())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(1000L))
                .claim("scope", scopes)
                .build();
        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now().plusSeconds(1000L), Map.of("alg", "RS256"), claims.getClaims());
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        JwtDto jwtDto = tokenService.getJwt(user);

        assertThat(jwtDto.accessToken()).isEqualTo(tokenValue);
    }

}
