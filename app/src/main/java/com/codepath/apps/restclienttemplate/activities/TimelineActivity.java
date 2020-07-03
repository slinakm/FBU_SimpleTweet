package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
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

    public final static boolean TESTING = false;
    public final static boolean SAVINGFORTESTING = true;

    private final int REQUEST_CODE = 20;
    public static final String TAG = "TimelineActivity";
    TwitterClient client;

    RecyclerView rvTweets;
    List<Tweet> tweetList;
    TweetsAdapter adapter;

    SwipeRefreshLayout swipeContainer;

    TweetDao tweetDao;

    ActivityTimelineBinding mainBinding;

    ProgressBar pbLoading;

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding
                = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        toolbar = mainBinding.toolbarMain;
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);
        tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().tweetDao();
        pbLoading = mainBinding.pbLoading;

        // Set up RecyclerView and Adapter
        rvTweets = mainBinding.rvTweets;
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
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.divider)));
        rvTweets.addItemDecoration(itemDecoration);


        // Set up Swipe Container
        swipeContainer = mainBinding.swipeContainer;
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

        // Query for existing tweets in the DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: received items from DB");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                adapter.clear();
                adapter.addAll(TweetWithUser.getTweetList(tweetWithUsers));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Send API call for new tweets (or used saved tweets if testing)
        populateHomeTimeline();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: Set up again");
        super.onRestart();
        recreate();
    }

    private void loadNextData(final int position) {
        final ProgressBar pbLoading = mainBinding.pbLoading;
        pbLoading.setVisibility(ProgressBar.VISIBLE);

        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`

        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess loadNextData: " + json.toString());
                pbLoading.setVisibility(ProgressBar.INVISIBLE);
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
                pbLoading.setVisibility(ProgressBar.INVISIBLE);
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
//            Intent intent = new Intent(this, ComposeActivity.class);
//            startActivityForResult(intent, REQUEST_CODE);
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment alertDialog = ComposeFragment.newInstance();
            alertDialog.show(fm, "fragment_alert");
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
        pbLoading.setVisibility(ProgressBar.VISIBLE);

       if (TESTING) {
           adapter.clear();
           loadItems();
           swipeContainer.setRefreshing(false);
           pbLoading.setVisibility(ProgressBar.INVISIBLE);
           adapter.notifyDataSetChanged();
           Log.d(TAG, "populateHomeTimeline: Testing  " + tweetList.toString());
       } else {
           client.getHomeTimelime(new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Headers headers, JSON json) {
                   Log.i(TAG, "onSuccess populateHomeTimeline: " + json.toString());
                   try {
                       final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(json.jsonArray);
                       adapter.clear();
                       adapter.addAll(tweetsFromNetwork);
                       swipeContainer.setRefreshing(false);
                       pbLoading.setVisibility(ProgressBar.INVISIBLE);

                       AsyncTask.execute(new Runnable() {
                           @Override
                           public void run() {
                               Log.i(TAG, "run (populateHomeTimeline): Saving data into database");
                               // Insert users first
                               tweetDao.insertModelForUsers(
                                       User.fromJsonTweetArray(tweetsFromNetwork).toArray(new User[0]));
                               // Then, insert tweets
                               tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
                           }
                       });
                   } catch (JSONException e) {
                       pbLoading.setVisibility(ProgressBar.INVISIBLE);
                       Log.e(TAG, "onSuccess populateHomeTimeline: Json Exception", e);
                       e.printStackTrace();
                   }
               }

               @Override
               public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                   pbLoading.setVisibility(ProgressBar.INVISIBLE);
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
