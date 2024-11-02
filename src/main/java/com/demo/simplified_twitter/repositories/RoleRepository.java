package com.demo.simplified_twitter.repositories;

import com.demo.simplified_twitter.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
