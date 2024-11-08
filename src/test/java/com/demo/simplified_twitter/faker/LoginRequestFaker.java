package com.demo.simplified_twitter.faker;

import com.demo.simplified_twitter.dto.LoginRequestDto;

public class LoginRequestFaker {

    public static LoginRequestDto fakeLoginRequestWithExistingUser() {
        return new LoginRequestDto("gustavo", "12345678");
    }

}
