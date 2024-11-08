package com.demo.simplified_twitter.faker;

import com.demo.simplified_twitter.entities.Role;

public class RoleEntityFaker {

    public static Role fakeRole(Role.Values roleLabel) {
        Role role = new Role();
        role.setId(1L);
        role.setName(roleLabel.name());
        return role;
    }
}
