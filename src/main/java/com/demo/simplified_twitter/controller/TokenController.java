package com.demo.simplified_twitter.controller;

import com.demo.simplified_twitter.dto.JwtDto;
import com.demo.simplified_twitter.dto.LoginRequestDto;
import com.demo.simplified_twitter.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TokenController {

    private final LoginService loginService;

    public TokenController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtDto login(@RequestBody LoginRequestDto loginRequest) {
        return loginService.login(loginRequest);
    }
}
