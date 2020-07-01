package com.codepath.apps.restclienttemplate.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;

@Entity
@Parcel
public class User implements Serializable {

    @ColumnInfo
    @PrimaryKey
    public Long id;

    @ColumnInfo
    public String name;

    @ColumnInfo
    public String screenName;

    @ColumnInfo
    public String profileImageURL;

    public User() {}
    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.setName(jsonObject.getString("name"));
        user.setScreenName(jsonObject.getString("screen_name"));
        user.setProfileImageURL(jsonObject.getString("profile_image_url_https"));
        user.setId(jsonObject.getLong("id"));
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }
}
