package com.codepath.apps.restclienttemplate.activities;

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
import com.codepath.apps.restclienttemplate.databinding.ActivityLoginBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {
    Tweet tweet;
    ActivityTweetDetailsBinding detailsBinding;

    final static String TAG = "TweetDetailsActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        detailsBinding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());

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

        Glide.with(this).
                load(R.drawable.ic_reply).
                transform(new RoundedCorners(60)).
                into(detailsBinding.ivReply);

        Glide.with(this).
                load(R.drawable.ic_retweet).
                transform(new RoundedCorners(60)).
                into(detailsBinding.ivRetweet);

//        if (tweet.isFavorited()) {
//            detailsBinding.ivLike.
//        }

        Glide.with(this).
                load(R.drawable.ic_heart).
                transform(new RoundedCorners(60)).
                into(detailsBinding.ivLike);

        detailsBinding.ivReply.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: replied!");
                Intent intent = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
                intent.putExtra("user", tweet.user.screenName);
                return true;
            }
        });

        detailsBinding.ivRetweet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: retweeted!");
                return true;
            }
        });

        detailsBinding.ivLike.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d(TAG, "onTouch: favorited!");
                return true;
            }
        });


    }

}
