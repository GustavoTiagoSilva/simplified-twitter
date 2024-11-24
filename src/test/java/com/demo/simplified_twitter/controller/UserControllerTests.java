package com.demo.simplified_twitter.controller;

import com.demo.simplified_twitter.config.SecurityConfig;
import com.demo.simplified_twitter.dto.CreateUserRequestDto;
import com.demo.simplified_twitter.dto.HttpErrorResponseDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserResponseDto;
import com.demo.simplified_twitter.exceptions.ResourceAlreadyExistsException;
import com.demo.simplified_twitter.faker.CreateUserRequestFaker;
import com.demo.simplified_twitter.faker.UserResponseDtoFaker;
import com.demo.simplified_twitter.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create an user")
    void shouldCreateAnUserWhenUserIsAuthenticated() throws Exception {
        CreateUserRequestDto createUserRequestDto = CreateUserRequestFaker.fakeCreateUserRequest();
        doNothing().when(userService).createUser(createUserRequestDto);

        var result = this.mockMvc.perform(
                post("/users")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createUserRequestDto)));

        assertThat(result).isNotNull();
        verify(userService, times(1)).createUser(createUserRequestDto);
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return all users when a user with admin permission is requesting the information")
    void shouldRetrieveAllUsersWhenAdminUserIsRequesting() throws Exception {
        List<UserResponseDto> expectedListOfUsersToBeReturned = UserResponseDtoFaker.fakeMany();
        when(userService.findAllUsers()).thenReturn(expectedListOfUsersToBeReturned);

        var httpResponse = this.mockMvc.perform(get("/users").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))).contentType(MediaType.APPLICATION_JSON_VALUE));
        var users = objectMapper.readValue(httpResponse.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<UserResponseDto>>() {
        });

        assertThat(httpResponse).isNotNull();
        assertThat(users).hasSameSizeAs(expectedListOfUsersToBeReturned);
        assertThat(users).usingRecursiveComparison().isEqualTo(expectedListOfUsersToBeReturned);
        httpResponse.andExpect(status().isOk());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("Should return [forbidden] when user does not have [ADMIN] permission")
    void shouldReturnForbiddenWhenUserDoesNotHaveAdminPermission() throws Exception {
        var httpResponse = this.mockMvc.perform(get("/users").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_BASIC"))).contentType(MediaType.APPLICATION_JSON_VALUE));

        httpResponse.andExpect(status().isForbidden());
        verify(userService, times(0)).findAllUsers();
    }

    @Test
    @DisplayName("Should return an empty list of users when no users were found")
    void shouldReturnEmptyListOfUsersWhenNoUsersWereFound() throws Exception {
        List<UserResponseDto> expectedListOfUsersToBeReturned = Collections.emptyList();
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        var httpResponse = this.mockMvc.perform(get("/users").with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))).contentType(MediaType.APPLICATION_JSON_VALUE));
        var users = objectMapper.readValue(httpResponse.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<UserResponseDto>>() {
        });

        assertThat(httpResponse).isNotNull();
        assertThat(users).hasSameSizeAs(expectedListOfUsersToBeReturned);
        httpResponse.andExpect(status().isOk());
        verify(userService, times(1)).findAllUsers();
    }

    @Test
    @DisplayName("Should return an error saying [ResourceAlreadyExists] when creating an user with existing username")
    void shouldReturnAnErrorSayingResourceAlreadyExistsWhenCreatingAnUserWithExistingUsername() throws Exception {
        CreateUserRequestDto createUserRequestDto = CreateUserRequestFaker.fakeCreateUserRequest();
        String errorMessage = "Username: " + createUserRequestDto.username() + " already exists";
        ResponseEntity<HttpErrorResponseDto> expectedHttpErrorResponse = ResponseEntity.of(Optional.of(
                new HttpErrorResponseDto(Instant.now(), HttpStatus.CONFLICT.value(), "Resource Already Exists", errorMessage, "/users")
        ));
        doThrow(new ResourceAlreadyExistsException(errorMessage)).when(userService).createUser(createUserRequestDto);

        var httpResponse = this.mockMvc.perform(
                post("/users")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(createUserRequestDto)));
        String httpResponseAsString = httpResponse.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        var createUserRequestErrorResponse = objectMapper.readValue(httpResponseAsString, HttpErrorResponseDto.class);

        assertThat(createUserRequestErrorResponse).isNotNull();
        assertThat(expectedHttpErrorResponse.getBody()).isEqualTo(createUserRequestErrorResponse);
        httpResponse.andExpect(status().isConflict());
    }
}
