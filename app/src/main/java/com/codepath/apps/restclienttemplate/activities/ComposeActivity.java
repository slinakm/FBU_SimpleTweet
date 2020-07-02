package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;
    EditText etCompose;
    com.google.android.material.textfield.TextInputLayout textInputLayout;
    Button btnTweet;
    Context context;
    ProgressBar pb;

    TwitterClient client;

    ActivityComposeBinding mainBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding
                = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        client = TwitterApp.getRestClient(this);

        context = this;
        etCompose = mainBinding.etCompose;
        textInputLayout = mainBinding.textInputLayout;
        textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);
        btnTweet = mainBinding.btnTweet;

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();

                if (tweetContent.isEmpty()) {
                    Toast.makeText(context, "Your tweet cannot be empty!",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(context, "Your tweet is over 140 characters!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                pb = mainBinding.pbLoading;
                pb.setVisibility(ProgressBar.VISIBLE);
                Toast.makeText(context, tweetContent,
                        Toast.LENGTH_LONG).show();
                // Make an API call to Twitter to publish the tweet and return tweet to parent
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");

                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "onSuccess: published tweet says" + tweet.getBody());

                            Intent intent = new Intent();
                            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            pb.setVisibility(ProgressBar.INVISIBLE);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        pb.setVisibility(ProgressBar.INVISIBLE);
                        Log.e(TAG, "onFailure to publish tweet ", throwable);
                    }
                });
            }
        });
    }
}
