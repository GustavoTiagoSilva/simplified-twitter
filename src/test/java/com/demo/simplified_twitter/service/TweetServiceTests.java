package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.CreateTweetRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.Tweet;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.exceptions.ResourceNotFoundException;
import com.demo.simplified_twitter.faker.UserEntityFaker;
import com.demo.simplified_twitter.repositories.TweetRepository;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TweetServiceTests {
    private final String applicationName = "simplified-twitter";
    private final TweetRepository tweetRepository = mock();
    private final UserRepository userRepository = mock();
    private final TweetService tweetService = new TweetService(tweetRepository, userRepository);

    @Test
    @DisplayName("Should create a tweet for a user when user is authenticated")
    void shouldCreateATweetForAUserWhenUserIsAuthenticated() {
        String tokenValue = "jwtToken";
        User userEntity = UserEntityFaker.fakeUserWithId(Role.Values.BASIC);
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
                .expiresAt(Instant.now())
                .issuedAt(Instant.now())
                .claim("scope", scopes)
                .build();
        Jwt jwt = new Jwt(tokenValue, Instant.now(), Instant.now(), Map.of("alg", "RS256"), claims.getClaims());
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
        CreateTweetRequestDto createTweetRequest = new CreateTweetRequestDto("Hello World");
        when(userRepository.findById(UUID.fromString(jwtAuthenticationToken.getName()))).thenReturn(Optional.of(userEntity));

        tweetService.createTweet(createTweetRequest, jwtAuthenticationToken);

        verify(userRepository, times(1)).findById(UUID.fromString(jwtAuthenticationToken.getName()));
        verify(tweetRepository, times(1)).save(new Tweet(null, userEntity, createTweetRequest.content()));
    }

    @Test
    @DisplayName("Should throw [ResourceNotFoundException] when non existing userId is infomrmed to create a tweet")
    void shouldThrowResourceNotFoundExceptionWhenNonExistingUserIdIsInformedToCreateATweet() {
        UUID nonExistingUserId = UUID.fromString("1646e721-f1f2-45c8-801b-38d5627d2044");
        var claims = JwtClaimsSet.builder()
                .issuer(this.applicationName)
                .subject(nonExistingUserId.toString())
                .expiresAt(Instant.now())
                .issuedAt(Instant.now())
                .build();
        Jwt jwt = new Jwt("token-value", Instant.now(), Instant.now(), Map.of("alg", "RS256"), claims.getClaims());
        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
        CreateTweetRequestDto createTweetRequest = new CreateTweetRequestDto("Hello World");
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> tweetService.createTweet(createTweetRequest, jwtAuthenticationToken));

        assertThat(exception.getMessage()).isEqualTo("User with id: " + nonExistingUserId + " not found");
        verify(userRepository, times(1)).findById(UUID.fromString(jwtAuthenticationToken.getName()));
        verify(tweetRepository, times(0)).save(any(Tweet.class));
    }
}
