package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.UserDto;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        var userEntity = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("User or password is invalid"));
        return new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword());
    }
}
