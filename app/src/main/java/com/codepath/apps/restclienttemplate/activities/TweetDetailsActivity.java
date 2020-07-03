package com.codepath.apps.restclienttemplate.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityLoginBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

public class TweetDetailsActivity extends AppCompatActivity {
    Tweet tweet;
    ActivityTweetDetailsBinding detailsBinding;
    TwitterClient client;

    final static String TAG = "TweetDetailsActivity";
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        detailsBinding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());

        client = TwitterApp.getRestClient(this);

        Log.d(TAG, String.format("Showing details for '%s'", tweet.getBody()));

        // Set up text and images on view.
        detailsBinding.tvName.setText(tweet.getUser().getName());
        detailsBinding.tvScreenName.setText(tweet.getUser().getScreenName());
        detailsBinding.tvRelativeTime.setText(tweet.getCreatedAt());
        detailsBinding.tvBody.setText(tweet.getBody());

        Glide.with(this).
                load(tweet.getUser().getProfileImageURL()).
                transform(new RoundedCorners(60)).
                placeholder(R.drawable.ic_person).
                into(detailsBinding.ivProfileImage);

        Log.i(TAG, "bind: " + tweet.isRetweeted() + tweet.isFavorited());

        if (tweet.isRetweeted()) {
            detailsBinding.ivRetweet.setActivated(true);
        } else {
            detailsBinding.ivRetweet.setActivated(false);
        }

        if (tweet.isFavorited()) {
            detailsBinding.ivLike.setActivated(true);
        } else {
            detailsBinding.ivLike.setActivated(false);
        }

        // Set on touch listeners
        detailsBinding.ivReply.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    final boolean isActivated = !detailsBinding.ivReply.isActivated();
                    detailsBinding.ivReply.setActivated(isActivated);

                    Log.d(TAG, "onTouch: replied!");
                    Intent intent = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                    intent.putExtra(TweetsAdapter.INTENT_USER_COMPOSE, tweet.user.getScreenName());

                    TweetDetailsActivity.this.startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        detailsBinding.ivRetweet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final boolean isActivated = !detailsBinding.ivRetweet.isActivated();
                    detailsBinding.ivRetweet.setActivated(isActivated);
                    tweet.setRetweeted(isActivated);

                    if (isActivated != tweet.isRetweeted()) {
                        Log.e(TAG, "onTouch: state of icon should be the same as the state of tweet");
                    }


                    Log.d(TAG, "onTouch: retweeted buttom pressed!");
                    if (TimelineActivity.TESTING) {
                        Log.i(TAG, "onSuccess: successfully retweeted or removed retweet!");
                    } else {
                        client.retweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess: successfully retweeted or removed retweet!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure: failed to retweet or remove retweet: " + response,  throwable);
                            }
                        }, isActivated, tweet.getId());
                    }
                    return true;
                }
                return false;
            }
        });

        detailsBinding.ivLike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();

                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    Log.i(TAG, "onTouch: " + tweet.isFavorited());

                    final boolean isActivated = !detailsBinding.ivLike.isActivated();
                    detailsBinding.ivLike.setActivated(isActivated);
                    tweet.setFavorited(isActivated);

                    if (isActivated != tweet.isFavorited()) {
                        Log.e(TAG, "onTouch: state of icon should be the same as the state of tweet");
                    }

                    Log.d(TAG, "onTouch: favorited!");
                    if (TimelineActivity.TESTING) {
                        Log.i(TAG, "onSuccess: successfully like or removed like!");
                    } else {
                        client.favoriteTweet(new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess: successfully like or removed like!");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure: failed to like or remove like: " + response,  throwable);
                            }
                        }, isActivated, tweet.getId());
                    }

                    return true;
                }
                return false;                }
        });


    }

}
