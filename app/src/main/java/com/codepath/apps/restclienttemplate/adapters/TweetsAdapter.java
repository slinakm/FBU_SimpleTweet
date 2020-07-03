package com.codepath.apps.restclienttemplate.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.activities.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    final public static String INTENT_USER_COMPOSE = "user";

    Context context;
    List<Tweet> tweetList;
    TwitterClient client;

    final String TAG = "TweetsAdapter";

    // Pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweetList) {
        this.context = context;
        this.tweetList = tweetList;
        client = TwitterApp.getRestClient(context);
    }

    // Inflate layout for each row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        ItemTweetBinding tweetBinding
                = ItemTweetBinding.inflate(LayoutInflater.from(context),
                parent, false);
        // Removed after we used View Binding library
        // View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetBinding);
    }

    // Bind values based on position of element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Tweet tweet = tweetList.get(position);
        holder.bind(tweet);
    }

    // Define a viewholder
    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return tweetList.size();
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        tweetList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweetList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemTweetBinding tweetBinding;


        public ViewHolder(@NonNull ItemTweetBinding tweetBinding) {
            super(tweetBinding.getRoot());
            this.tweetBinding = tweetBinding;
            tweetBinding.getRoot().setOnClickListener(this);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(final Tweet tweet) {

            // Bind images and text
            tweetBinding.tvBody.setText(tweet.getBody());
            tweetBinding.tvName.setText(tweet.getUser().getName());
            tweetBinding.tvScreenName.setText(tweet.getUser().getScreenName());
            tweetBinding.tvRelativeTime.setText(tweet.getCreatedAt());
            Glide.with(context).
                    load(tweet.getUser().getProfileImageURL()).
                    transform(new RoundedCorners(10)).
                    into(tweetBinding.ivProfileImage);

            if (tweet.containsMedia) {
                tweetBinding.media.setVisibility(View.VISIBLE);
                Glide.with(context).
                        load(tweet.getMediaURL()).
                        transform(new RoundedCorners(10)).
                        into(tweetBinding.media);
            } else {
                tweetBinding.media.setVisibility(View.GONE);
            }

            Log.i(TAG, "bind: " + tweet.isRetweeted() + tweet.isFavorited());

            Glide.with(context).
                    load(tweet.getUser().getProfileImageURL()).
                    transform(new RoundedCorners(60)).
                    placeholder(R.drawable.ic_person).
                    into(tweetBinding.ivProfileImage);

            if (tweet.isRetweeted()) {
                tweetBinding.ivRetweet.setActivated(true);
            } else {
                tweetBinding.ivRetweet.setActivated(false);
            }

            if (tweet.isFavorited()) {
                tweetBinding.ivLike.setActivated(true);
            } else {
                tweetBinding.ivLike.setActivated(false);
            }

            // Set on touch listeners
            tweetBinding.ivReply.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        boolean isActivated = !tweetBinding.ivReply.isActivated();
                        tweetBinding.ivReply.setActivated(isActivated);

                        Log.d(TAG, "onTouch: replied!");
                        Intent intent = new Intent(context, ComposeActivity.class);
                        intent.putExtra(INTENT_USER_COMPOSE, tweet.user.getScreenName());

                        context.startActivity(intent);
                        return true;
                    }
                    return false;
                }
            });

            tweetBinding.ivRetweet.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        final boolean isActivated = !tweetBinding.ivRetweet.isActivated();
                        tweetBinding.ivRetweet.setActivated(isActivated);
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

            tweetBinding.ivLike.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    view.performClick();

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        final boolean isActivated = !tweetBinding.ivLike.isActivated();
                        tweetBinding.ivLike.setActivated(isActivated);
                        tweet.setFavorited(isActivated);

                        if (isActivated != tweet.isFavorited()) {
                            Log.e(TAG, "onTouch: state of icon should be the same as the state of tweet"
                                    + tweet.isFavorited());
                        }

                        Log.d(TAG, "onTouch: favorited!");
                        if (TimelineActivity.TESTING) {
                            Log.i(TAG, "onSuccess: successfully like or removed like!");
                        } else {
                            client.favoriteTweet(new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Headers headers, JSON json) {
                                    Log.i(TAG, "onSuccess: successfully like or removed like!");
                                    tweetBinding.ivLike.setActivated(isActivated);
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


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweetList.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }
}
