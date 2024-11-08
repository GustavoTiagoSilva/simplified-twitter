package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.CreateUserRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import com.demo.simplified_twitter.exceptions.ResourceAlreadyExistsException;
import com.demo.simplified_twitter.exceptions.ResourceNotFoundException;
import com.demo.simplified_twitter.faker.CreateUserRequestFaker;
import com.demo.simplified_twitter.faker.RoleEntityFaker;
import com.demo.simplified_twitter.faker.UserEntityFaker;
import com.demo.simplified_twitter.repositories.RoleRepository;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTests {
    private final UserRepository userRepository = mock();
    private final RoleRepository roleRepository = mock();
    private final PasswordEncoder passwordEncoder = mock();
    private final UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

    @Test
    @DisplayName("Should return a user when filtering by an existing username")
    void shouldReturnAUserWhenFilteringByAnExistingUserName() {
        String existingUsername = "gustavo";
        Role adminRole = RoleEntityFaker.fakeRole(Role.Values.ADMIN);
        User user = UserEntityFaker.fakeUserWithId(Role.Values.ADMIN);
        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(Role.Values.ADMIN.name())).thenReturn(Optional.of(adminRole));
        UserDto expectedUserToBeReturned = new UserDto(user.getId(), user.getUsername(), user.getPassword(), Set.of(new RoleDto(1L, Role.Values.ADMIN.name())));

        UserDto userDto = userService.findByUsername(existingUsername);

        assertThat(userDto).isEqualTo(expectedUserToBeReturned);
    }

    @Test
    @DisplayName("Should throw [BadCredentialsException] when invalid username is informed")
    void shouldThrowBadCredentialsExceptionWhenInvalidUsernameIsInformed() {
        String invalidUsername = "gustavo";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        var exception = assertThrows(BadCredentialsException.class, () -> userService.findByUsername(invalidUsername));

        assertThat(exception.getMessage()).isEqualTo("User or password is invalid");
    }

    @Test
    @DisplayName("Should create a new user when valid data is informed")
    void shouldCreateANewUserWhenValidDataIsInformed() {
        var role = Role.Values.BASIC;
        Role basicRole = RoleEntityFaker.fakeRole(role);
        CreateUserRequestDto createUserRequest = CreateUserRequestFaker.fakeCreateUserRequest();
        User userBeforeSaving = UserEntityFaker.fakeUserWithoutId(role);
        User userAfterSaving = UserEntityFaker.fakeUserWithId(role);
        String encodedPassword = "$2a$12$va2eeKAPHk3vzozIeA4uWuehAulFh/6X9JTrFXSMTMm2sDlZ7BXVW";
        when(userRepository.findByUsername(createUserRequest.username())).thenReturn(Optional.empty());
        when(roleRepository.findByName(role.name())).thenReturn(Optional.of(basicRole));
        when(passwordEncoder.encode(createUserRequest.password())).thenReturn(encodedPassword);
        when(userRepository.save(userBeforeSaving)).thenReturn(userAfterSaving);

        userService.createUser(createUserRequest);

        verify(userRepository, times(1)).save(userBeforeSaving);
        verify(roleRepository, times(1)).findByName(role.name());
    }

    @Test
    @DisplayName("Should throw [ResourceAlreadyExistsException] when creating a user with existing username")
    void shouldThrowResourceAlreadyExistsWhenCreatingAUserWithExistingUsername() {
        User existingUser = UserEntityFaker.fakeUserWithId(Role.Values.ADMIN);
        CreateUserRequestDto createUserRequest = CreateUserRequestFaker.fakeCreateUserRequest();
        when(userRepository.findByUsername(createUserRequest.username())).thenReturn(Optional.of(existingUser));

        var exception = assertThrows(ResourceAlreadyExistsException.class, () -> userService.createUser(createUserRequest));

        assertThat(exception.getMessage()).isEqualTo("Username: " + existingUser.getUsername() + " already exists");
    }

    @Test
    @DisplayName("Should throw [ResourceNotFoundException] when non existing role name is filtered")
    void shouldThrowResourceNotFoundExceptionWhenNonExistingRoleNameIsFiltered() {
        String invalidRoleName = "SUPER_USER";
        CreateUserRequestDto createUserRequest = CreateUserRequestFaker.fakeCreateUserRequest();
        when(userRepository.findByUsername(createUserRequest.username())).thenReturn(Optional.empty());
        when(roleRepository.findByName(invalidRoleName)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> userService.createUser(createUserRequest));

        assertThat(exception.getMessage()).isEqualTo("Role not found");
    }

    @Test
    @DisplayName("Should return all users")
    void shouldReturnAllUsers() {
        List<User> fakeUsers = UserEntityFaker.fakeManyUsersWithId(Role.Values.ADMIN);
        when(userRepository.findAll()).thenReturn(fakeUsers);

        var users = userService.findAllUsers();

        assertThat(users.size()).isEqualTo(fakeUsers.size());
    }
}
