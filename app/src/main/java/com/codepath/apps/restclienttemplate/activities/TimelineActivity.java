package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public final boolean TESTING = true;
    public final boolean SAVINGFORTESTING = true;

    private final int REQUEST_CODE = 20;
    public static final String TAG = "TimelineActivity";
    TwitterClient client;

    RecyclerView rvTweets;
    List<Tweet> tweetList;
    TweetsAdapter adapter;

    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // Set up RecyclerView
        rvTweets = findViewById(R.id.rvTweets);
        tweetList = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweetList);

        LinearLayoutManager rvLinearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(rvLinearLayoutManager);
        rvTweets.setAdapter(adapter);
        final EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener
                = new EndlessRecyclerViewScrollListener(rvLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (TESTING) {
                    loadMoreItems();
                } else {
                    loadNextData(page);
                }
            }
        };
        rvTweets.addOnScrollListener(endlessRecyclerViewScrollListener);

        // Set up Swipe Container
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: fetching new data");
                populateHomeTimeline();

                if (!TESTING
                        && SAVINGFORTESTING) {
                    saveItems();
                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateHomeTimeline();
    }

    private void loadNextData(final int position) {
        final ProgressBar pb = findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess loadNextData: " + json.toString());
                pb.setVisibility(ProgressBar.INVISIBLE);
                try {
                    List<Tweet> tweets = Tweet.fromJsonArray(json.jsonArray);
                    adapter.addAll(tweets);
                } catch (JSONException e) {
                    Log.e(TAG, "onSuccess loadNextData: Json Exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                pb.setVisibility(ProgressBar.INVISIBLE);
                Log.e(TAG, "onFailure loadNextData: " + response, throwable);
            }
        }, tweetList.get(tweetList.size()-1).getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!TESTING
                && SAVINGFORTESTING) {
            saveItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == this.REQUEST_CODE
                && resultCode == RESULT_OK) {
            // Get data from tweet and update RV with new tweet
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            tweetList.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        final ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);

       if (TESTING) {
           adapter.clear();
           loadItems();
           swipeContainer.setRefreshing(false);
           pb.setVisibility(ProgressBar.INVISIBLE);
           Log.d(TAG, "populateHomeTimeline: Testing  " + tweetList.toString());
       } else {
           client.getHomeTimelime(new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Headers headers, JSON json) {
                   Log.i(TAG, "onSuccess populateHomeTimeline: " + json.toString());
                   try {
                       adapter.clear();
                       adapter.addAll(Tweet.fromJsonArray(json.jsonArray));
                       swipeContainer.setRefreshing(false);
                       pb.setVisibility(ProgressBar.INVISIBLE);
                   } catch (JSONException e) {
                       pb.setVisibility(ProgressBar.INVISIBLE);
                       Log.e(TAG, "onSuccess populateHomeTimeline: Json Exception", e);
                       e.printStackTrace();
                   }
               }

               @Override
               public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                   pb.setVisibility(ProgressBar.INVISIBLE);
                   Log.e(TAG, "onFailure populateHomeTimeline: " + response, throwable);
               }
           });
       }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }


    // This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            //Reading of object in a file
            FileInputStream file = new FileInputStream(getDataFile());
            ObjectInputStream in = new ObjectInputStream(file);

            List<Tweet> tempList = (List<Tweet>) in.readObject();

            in.close();
            file.close();

            tweetList.clear();
            tweetList.addAll(tempList);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("MainActivity", "Error reading items", e);
            tweetList.clear();
        }
        adapter.notifyDataSetChanged();
    }

    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(getDataFile());
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(tweetList);

            out.close();
            file.close();
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e); }
    }

    private void loadMoreItems() {
        try {

            //Reading of object in a file
            FileInputStream file = new FileInputStream(getDataFile());
            ObjectInputStream in = new ObjectInputStream(file);

            List<Tweet> tempList = (List<Tweet>) in.readObject();

            in.close();
            file.close();

            tweetList.addAll(tempList);
            adapter.notifyItemRangeInserted(tweetList.size()-tempList.size()-1,
                    tempList.size());
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "Error reading items in load more items", e);
            tweetList.clear();
            adapter.notifyDataSetChanged();
        }
    }
}
