package com.demo.simplified_twitter.dto;

import java.util.UUID;

public record UserDto(UUID id, String username, String password) {
}
