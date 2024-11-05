package com.demo.simplified_twitter.repositories;

import com.demo.simplified_twitter.entities.Tweet;
import com.demo.simplified_twitter.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Optional<Tweet> findByIdAndUser(Long id, User user);
}
