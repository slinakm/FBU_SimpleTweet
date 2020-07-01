package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;

    public long id;

    public boolean containsMedia;
    public String mediaURL;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.setBody(jsonObject.getString("text"));
        tweet.setCreatedAt(jsonObject.getString("created_at"));
        tweet.setId(jsonObject.getLong("id"));
        tweet.setUser(User.fromJson(jsonObject.getJSONObject("user")));

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
    public String getRelativeTimeAgo(String rawJsonDate) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
