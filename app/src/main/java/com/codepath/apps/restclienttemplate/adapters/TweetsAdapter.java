package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweetList;

    final String TAG = "TweetsAdapter";

    // Pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweetList) {
        this.context = context;
        this.tweetList = tweetList;
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

        public void bind(Tweet tweet) {
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
            }


            Glide.with(context).
                    load(tweet.getUser().getProfileImageURL()).
                    transform(new RoundedCorners(60)).
                    placeholder(R.drawable.ic_person).
                    into(tweetBinding.ivProfileImage);

            Glide.with(context).
                    load(R.drawable.ic_reply).
                    transform(new RoundedCorners(60)).
                    into(tweetBinding.ivReply);

            Glide.with(context).
                    load(R.drawable.ic_retweet).
                    transform(new RoundedCorners(60)).
                    into(tweetBinding.ivRetweet);

            Glide.with(context).
                    load(R.drawable.ic_heart).
                    transform(new RoundedCorners(60)).
                    into(tweetBinding.ivLike);

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
