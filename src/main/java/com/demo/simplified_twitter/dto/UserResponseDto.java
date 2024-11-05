package com.demo.simplified_twitter.dto;

import java.util.Set;

public record UserResponseDto(String username, Set<RoleDto> role) {
}
