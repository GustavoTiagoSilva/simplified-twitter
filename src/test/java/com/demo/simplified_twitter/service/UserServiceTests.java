package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.CreateUserRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import com.demo.simplified_twitter.repositories.RoleRepository;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    private final UserRepository userRepository = mock();
    private final RoleRepository roleRepository = mock();
    private final PasswordEncoder passwordEncoder = mock();
    private final UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

    @Test
    @DisplayName("Should return a user when filtering by an existing username")
    void shouldReturnAUserWhenFilteringByAnExistingUserName() {
        String existingUsername = "gustavo";
        String password = "12345678";
        UUID userId = UUID.randomUUID();
        Role adminRole = new Role();
        adminRole.setId(1L);
        adminRole.setName(Role.Values.ADMIN.name());
        User user = new User();
        user.setId(userId);
        user.setUsername(existingUsername);
        user.setPassword(password);
        user.setRoles(Set.of(adminRole));
        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.Values.ADMIN.name())).thenReturn(Optional.of(adminRole));
        UserDto expectedUserToBeReturned = new UserDto(userId, existingUsername, password, Set.of(new RoleDto(1L, Role.Values.ADMIN.name())));

        UserDto userDto = userService.findByUsername(existingUsername);

        assertThat(userDto).isEqualTo(expectedUserToBeReturned);
    }

    @Test
    @DisplayName("Should throw [Bad Credentials Exception] when invalid username is informed")
    void shouldThrowBadCredentialsExceptionWhenInvalidUsernameIsInformed() {
        String invalidUsername = "gustavo";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(BadCredentialsException.class, () -> userService.findByUsername(invalidUsername));

        assertThat(exception.getMessage()).isEqualTo("User or password is invalid");
    }

    @Test
    @DisplayName("Should create a new user when valid data is informed")
    void shouldCreateANewUserWhenValidDataIsInformed() {
        String username = "gustavo";
        String notEncodedPassword = "12345678";
        String encodedPassword = "$2a$12$va2eeKAPHk3vzozIeA4uWuehAulFh/6X9JTrFXSMTMm2sDlZ7BXVW";
        UUID userId = UUID.randomUUID();
        Role basicRole = new Role();
        basicRole.setId(2L);
        basicRole.setName(Role.Values.BASIC.name());
        CreateUserRequestDto createUserRequest = new CreateUserRequestDto(username, notEncodedPassword);
        User userBeforeSaving = new User();
        userBeforeSaving.setId(null);
        userBeforeSaving.setUsername(username);
        userBeforeSaving.setPassword(encodedPassword);
        userBeforeSaving.setRoles(Set.of(basicRole));
        User expectedUserAfterSaving = new User();
        expectedUserAfterSaving.setId(userId);
        expectedUserAfterSaving.setUsername(createUserRequest.username());
        expectedUserAfterSaving.setPassword(encodedPassword);
        expectedUserAfterSaving.setRoles(Set.of(basicRole));
        when(userRepository.findByUsername(createUserRequest.username())).thenReturn(Optional.empty());
        when(roleRepository.findByName(Role.Values.BASIC.name())).thenReturn(Optional.of(basicRole));
        when(passwordEncoder.encode(notEncodedPassword)).thenReturn(encodedPassword);
        when(userRepository.save(userBeforeSaving)).thenReturn(expectedUserAfterSaving);

        userService.createUser(createUserRequest);

        verify(userRepository, times(1)).save(userBeforeSaving);
    }
}
