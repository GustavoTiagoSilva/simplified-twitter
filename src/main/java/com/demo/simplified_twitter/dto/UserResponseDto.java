package com.demo.simplified_twitter.dto;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(UUID id, String username, Set<RoleDto> role) {
}
