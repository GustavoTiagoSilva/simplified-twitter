package com.demo.simplified_twitter.controller;

import com.demo.simplified_twitter.config.SecurityConfig;
import com.demo.simplified_twitter.dto.CreateUserRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserResponseDto;
import com.demo.simplified_twitter.faker.CreateUserRequestFaker;
import com.demo.simplified_twitter.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Should create an user")
    void shouldCreateAnUserWhenUserIsAuthenticated() throws Exception {
        CreateUserRequestDto createUserRequestDto = CreateUserRequestFaker.fakeCreateUserRequest();
        doNothing().when(userService).createUser(createUserRequestDto);

        var result = this.mockMvc.perform(
                post("/users")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(createUserRequestDto)));

        assertThat(result).isNotNull();
        verify(userService, times(1)).createUser(createUserRequestDto);
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return all users when a user with admin permission is requesting the information")
    void shouldRetrieveAllUsersWhenAdminUserIsRequesting() throws Exception {
        List<UserResponseDto> expectedListOfUsersToBeReturned = new ArrayList<>();
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "gustavo", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "henrique", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "carol", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "paula", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "daniel", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "pedro", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "joão", Set.of(new RoleDto(1L, "ADMIN"))));
        when(userService.findAllUsers()).thenReturn(expectedListOfUsersToBeReturned);

        var httpResponse = this.mockMvc.perform(get("/users").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))).contentType(MediaType.APPLICATION_JSON_VALUE));
        var users = new ObjectMapper().readValue(httpResponse.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<UserResponseDto>>() {
        });

        assertThat(httpResponse).isNotNull();
        assertThat(users).hasSameSizeAs(expectedListOfUsersToBeReturned);
        assertThat(users).usingRecursiveComparison().isEqualTo(expectedListOfUsersToBeReturned);
        httpResponse.andExpect(status().isOk());
        verify(userService, times(1)).findAllUsers();
    }
}
