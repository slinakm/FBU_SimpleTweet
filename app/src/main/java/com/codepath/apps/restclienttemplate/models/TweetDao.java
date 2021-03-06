package com.codepath.apps.restclienttemplate.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TweetDao {

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id as tweet_id," +
            "Tweet.containsMedia as tweet_containsMedia, Tweet.mediaURL as tweet_mediaURL, Tweet.favorited as tweet_favorited," +
            "Tweet.retweeted as tweet_retweeted, Tweet.userId as tweet_userId, User.*"
            + " FROM Tweet INNER JOIN User ON Tweet.userId = User.id ORDER BY createdAt DESC LIMIT 5")
    List<TweetWithUser> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModel(Tweet... tweets);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModelForUsers(User... users);

}
