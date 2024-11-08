package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.JwtDto;
import com.demo.simplified_twitter.dto.LoginRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.faker.LoginRequestFaker;
import com.demo.simplified_twitter.faker.UserEntityFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginServiceTests {
    private final UserService userService = mock();
    private final TokenService tokenService = mock();
    private final PasswordEncoder passwordEncoder = mock();
    private final LoginService loginService = new LoginService(userService, tokenService, passwordEncoder);

    @Test
    @DisplayName("Should return a valid jwt access token when existing user is informed")
    void shouldReturnAValidJwtAccessTokenWhenExistingUserIsInformed() {
        LoginRequestDto loginRequest = LoginRequestFaker.fakeLoginRequestWithExistingUser();
        User userEntity = UserEntityFaker.fakeUserWithId(Role.Values.ADMIN);
        Set<RoleDto> roles = userEntity.getRoles().stream().map(role -> new RoleDto(role.getId(), role.getName())).collect(Collectors.toSet());
        UserDto user = new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), roles);
        JwtDto expectedJwtToBeReturned = new JwtDto("accessToken", 86400L);
        when(userService.findByUsername(loginRequest.username())).thenReturn(user);
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(Boolean.TRUE);
        when(tokenService.getJwt(user)).thenReturn(expectedJwtToBeReturned);

        var jwt = loginService.login(loginRequest);

        assertThat(jwt).isEqualTo(expectedJwtToBeReturned);
        verify(userService, times(1)).findByUsername(loginRequest.username());
    }
}
