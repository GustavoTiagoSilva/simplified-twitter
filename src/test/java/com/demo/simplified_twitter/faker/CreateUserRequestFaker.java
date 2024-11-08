package com.demo.simplified_twitter.faker;

import com.demo.simplified_twitter.dto.CreateUserRequestDto;

public class CreateUserRequestFaker {
    public static CreateUserRequestDto fakeCreateUserRequest() {
        String username = "gustavo";
        String nonEncodedPassword = "12345678";
        return new CreateUserRequestDto(username, nonEncodedPassword);
    }
}
