package com.demo.simplified_twitter.faker;

import com.demo.simplified_twitter.entities.Role;
import com.demo.simplified_twitter.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UserEntityFaker {

    private static final String ENCODED_PASSWORD = "$2a$12$va2eeKAPHk3vzozIeA4uWuehAulFh/6X9JTrFXSMTMm2sDlZ7BXVW";

    public static User fakeUserWithoutId(Role.Values role) {
        User user = new User();
        user.setId(null);
        user.setUsername("gustavo");
        user.setPassword(ENCODED_PASSWORD);
        user.setRoles(Set.of(RoleEntityFaker.fakeRole(role)));
        return user;
    }

    public static User fakeUserWithId(Role.Values role) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("gustavo");
        user.setPassword(ENCODED_PASSWORD);
        user.setRoles(Set.of(RoleEntityFaker.fakeRole(role)));
        return user;
    }

    public static List<User> fakeManyUsersWithId(Role.Values role) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername("gustavo_" + i);
            user.setPassword(ENCODED_PASSWORD);
            user.setRoles(Set.of(RoleEntityFaker.fakeRole(role)));
            users.add(user);
        }
        return users;
    }
}
