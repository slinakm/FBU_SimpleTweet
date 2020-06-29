package com.codepath.apps.restclienttemplate;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.scribejava.apis.TwitterApi;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        populateHomeTimeline();
    }

    private void populateHomeTimeline() {

        
    }
}
