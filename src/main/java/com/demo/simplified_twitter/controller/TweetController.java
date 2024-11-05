package com.demo.simplified_twitter.controller;

import com.demo.simplified_twitter.dto.CreateTweetRequestDto;
import com.demo.simplified_twitter.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTweet(@RequestBody CreateTweetRequestDto createTweetRequest, JwtAuthenticationToken jwtAuthenticationToken) {
        tweetService.createTweet(createTweetRequest, jwtAuthenticationToken);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTweet(@PathVariable Long id, JwtAuthenticationToken jwtAuthenticationToken) {
        tweetService.deleteTweet(id, jwtAuthenticationToken);
    }

}
