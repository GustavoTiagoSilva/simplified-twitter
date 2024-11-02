package com.demo.simplified_twitter.repositories;

import com.demo.simplified_twitter.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
