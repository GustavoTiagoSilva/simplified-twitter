package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.JwtDto;
import com.demo.simplified_twitter.dto.LoginRequestDto;
import com.demo.simplified_twitter.exceptions.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserService userService,
                        TokenService tokenService,
                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public JwtDto login(LoginRequestDto loginRequest) {
        var user = userService.findByUsername(loginRequest.username());
        if (!this.isLoginCorrect(loginRequest, user.password())) {
            throw new BadCredentialsException("User or password is invalid");
        }
        return tokenService.getJwt(user);
    }

    private boolean isLoginCorrect(LoginRequestDto loginRequest, String password) {
        return passwordEncoder.matches(loginRequest.password(), password);
    }
}
