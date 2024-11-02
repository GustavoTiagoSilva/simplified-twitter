package com.demo.simplified_twitter.repositories;

import com.demo.simplified_twitter.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
}
