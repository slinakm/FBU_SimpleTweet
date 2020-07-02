package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
@Parcel
public class Tweet implements Serializable {

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public boolean containsMedia;

    @ColumnInfo
    public String mediaURL;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public boolean favorited;

    @Ignore
    public User user;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.setBody(jsonObject.getString("text"));
        tweet.setCreatedAt(jsonObject.getString("created_at"));
        tweet.setId(jsonObject.getLong("id"));

        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.setUser(user);
        tweet.setUserId(user.getId());

        tweet.setContainsMedia(false);
        JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            tweet.setContainsMedia(true);
            JSONArray media = entities.getJSONArray("media");
            JSONObject mediaObject = (JSONObject) media.get(0);
            tweet.setMediaURL(mediaObject.getString("media_url_https"));
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setContainsMedia(boolean containsMedia) {
        this.containsMedia = containsMedia;
    }

    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    private String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return getRelativeTimeAgo(createdAt);
    }

    public User getUser() {
        return user;
    }

    public boolean containsMedia() {
        return containsMedia;
    }

    public String getMediaURL() {
        return mediaURL;
    }
}
