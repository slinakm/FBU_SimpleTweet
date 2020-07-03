package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.FollowsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityProfileBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileActivity extends AppCompatActivity {

    User user;
    private List<User> userList;
    RecyclerView rvFollow;
    ActivityProfileBinding detailsBinding;
    TwitterClient client;

    final static String TAG = "ProfileActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        detailsBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());
        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        Log.d(TAG, "onCreate: " + user.getName());
        rvFollow = detailsBinding.rvFollows;
        userList = new ArrayList<>();

        client = TwitterApp.getRestClient(this);

        // Set up text and images on view.
        detailsBinding.tvName.setText(user.getName());
        detailsBinding.tvScreenName.setText(user.getScreenName());

        Glide.with(this).
                load(user.getProfileImageURL()).
                transform(new RoundedCorners(30)).
                placeholder(R.drawable.ic_person).
                into(detailsBinding.ivProfileImage);

        final FollowsAdapter adapter = new FollowsAdapter(this, userList);

        rvFollow.setAdapter(adapter);
        rvFollow.setLayoutManager(new LinearLayoutManager(this));
        detailsBinding.btnFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.getFollowers(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        adapter.clear();
                        List<User> users = null;
                        try {
                            Log.d(TAG, "onSuccess: " + json.toString());
                            users = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                        } catch (JSONException e) {
                            Log.e(TAG, "onSuccess: json exception", e);
                            e.printStackTrace();
                        }
                        adapter.addAll(users);
                        Log.i(TAG, "onSuccess: follower");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers,
                                          String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: follower" + response, throwable);
                    }
                }, user.getId());
            }
        });

        detailsBinding.btnFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.getFollowing(new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            Log.d(TAG, "onSuccess: " + json.toString());

                            adapter.clear();
                            List<User> users = User.fromJsonArray(json.jsonObject.getJSONArray("users"));
                            adapter.addAll(users);
                        } catch (JSONException e) {
                            Log.e(TAG, "onSuccess: json exception", e);
                            e.printStackTrace();
                        }

                        Log.i(TAG, "onSuccess: following");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers,
                                          String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: following" + response, throwable);
                    }
                }, user.getId());
            }
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
