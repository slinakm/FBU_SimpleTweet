package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    public String name;
    public String screenName;
    public String profileImageURL;

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.setName(jsonObject.getString("name"));
        user.setScreenName(jsonObject.getString("screen_name"));
        user.setProfileImageURL(jsonObject.getString("profile_image_url_https"));
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
