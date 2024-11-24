package com.demo.simplified_twitter.faker;

import com.demo.simplified_twitter.dto.RoleDto;
import com.demo.simplified_twitter.dto.UserResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserResponseDtoFaker {

    public static List<UserResponseDto> fakeMany() {
        List<UserResponseDto> expectedListOfUsersToBeReturned = new ArrayList<>();
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "gustavo", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "henrique", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "carol", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "paula", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "daniel", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "pedro", Set.of(new RoleDto(1L, "ADMIN"))));
        expectedListOfUsersToBeReturned.add(new UserResponseDto(UUID.randomUUID(), "joão", Set.of(new RoleDto(1L, "ADMIN"))));
        return expectedListOfUsersToBeReturned;
    }

}
