package com.demo.simplified_twitter.service;

import com.demo.simplified_twitter.dto.CreateTweetDto;
import com.demo.simplified_twitter.entities.Tweet;
import com.demo.simplified_twitter.exceptions.ResourceNotFoundException;
import com.demo.simplified_twitter.repositories.TweetRepository;
import com.demo.simplified_twitter.repositories.UserRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TweetService {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTweet(CreateTweetDto createTweetRequest, JwtAuthenticationToken jwtAuthenticationToken) {
        var user = userRepository
                .findById(UUID.fromString(jwtAuthenticationToken.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + jwtAuthenticationToken.getName() + " not found"));
        tweetRepository.save(new Tweet(null, user, createTweetRequest.content()));
    }
}
