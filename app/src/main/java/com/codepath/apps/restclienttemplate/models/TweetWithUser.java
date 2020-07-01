package com.codepath.apps.restclienttemplate.models;

import androidx.room.Embedded;

import java.util.ArrayList;
import java.util.List;

public class TweetWithUser {

    @Embedded
    User user;

    @Embedded(prefix = "tweet_")
    Tweet tweet;

    public static List<Tweet> getTweetList(List<TweetWithUser> tweetWithUsers) {
        List<Tweet> tweetList = new ArrayList<>();
        for (TweetWithUser tu: tweetWithUsers) {
            Tweet tweet = tu.tweet;
            tweet.setUser(tu.user);
            tweetList.add(tweet);
        }
        return tweetList;
    }
}
