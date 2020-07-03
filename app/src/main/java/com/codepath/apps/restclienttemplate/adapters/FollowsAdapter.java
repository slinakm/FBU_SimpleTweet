package com.codepath.apps.restclienttemplate.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ItemFollowerBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import java.util.List;

public class FollowsAdapter extends RecyclerView.Adapter<FollowsAdapter.ViewHolder> {

    private List<com.codepath.apps.restclienttemplate.models.User> userList;
    private Activity context;
    static final String TAG = "FollowsAdapter";

    public FollowsAdapter(Activity context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        ItemFollowerBinding binding =
                ItemFollowerBinding.inflate(context.getLayoutInflater(), parent, false);
        // Return a new holder instance
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.bind(userList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return userList.size();
    }

    // View lookup cache
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemFollowerBinding binding;

        public ViewHolder(final ItemFollowerBinding binding) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(binding.getRoot());

            this.binding = binding;

        }

        public void bind(User user) {
            binding.tvName.setText(user.getName());
            binding.tvScreenName.setText(user.getScreenName());
        }
    }

    /* Within the RecyclerView.Adapter class */

    // Clean all elements of the recycler
    public void clear() {
        userList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        userList.addAll(list);
        notifyDataSetChanged();
    }
}
