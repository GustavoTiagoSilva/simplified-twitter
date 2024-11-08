package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.CreateUserRequestDto;
import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.dto.UserResponseDto;
import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import com.demo.simplified_twitter.exceptions.ResourceAlreadyExistsException;
import com.demo.simplified_twitter.exceptions.ResourceNotFoundException;
import com.demo.simplified_twitter.repositories.RoleRepository;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        var userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User or password is invalid"));
        var roles = userEntity
                .getRoles()
                .stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
        return new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), roles);
    }

    @Transactional
    public void createUser(CreateUserRequestDto createUserRequest) {
        userRepository.findByUsername(createUserRequest.username()).ifPresentOrElse(user -> {
            log.error("Username '{}' already exists", user.getUsername());
            throw new ResourceAlreadyExistsException("Username: " + user.getUsername() + " already exists");
        }, () -> {
            var exception = new ResourceNotFoundException("Role not found");
            User newUser = new User();
            Role role = roleRepository
                    .findByName(Role.Values.BASIC.name())
                    .orElseThrow(() -> exception);
            newUser.setPassword(passwordEncoder.encode(createUserRequest.password()));
            newUser.setRoles(Set.of(role));
            newUser.setUsername(createUserRequest.username());
            userRepository.save(newUser);
        });
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserResponseDto(user.getUsername(), user.getRoles().stream().map(role -> new RoleDto(role.getId(), role.getName())).collect(Collectors.toSet()))).toList();
    }
}
