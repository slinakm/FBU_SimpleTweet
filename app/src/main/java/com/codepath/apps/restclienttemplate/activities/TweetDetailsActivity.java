package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        detailsBinding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());

        Log.d("TweetDetailsActivity", String.format("Showing details for '%s'", tweet.getBody()));

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

        Glide.with(this).
                load(R.drawable.ic_heart).
                transform(new RoundedCorners(60)).
                into(detailsBinding.ivLike);

    }
}
