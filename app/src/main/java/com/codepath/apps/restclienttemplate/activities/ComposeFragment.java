package com.codepath.apps.restclienttemplate.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapters.TweetsAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;
    EditText etCompose;
    com.google.android.material.textfield.TextInputLayout textInputLayout;
    Button btnTweet;
    Context context;
    ProgressBar pb;

    TwitterClient client;

    ActivityComposeBinding mainBinding;

    public ComposeFragment(){}

    public static ComposeFragment newInstance() {

        Bundle args = new Bundle();

        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        mainBinding
                = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        client = TwitterApp.getRestClient(this);

        // Set up binding views
        context = this;
        etCompose = mainBinding.etCompose;
        textInputLayout = mainBinding.textInputLayout;
        textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);
        btnTweet = mainBinding.btnTweet;


        if (getArguments().containsKey(TweetsAdapter.INTENT_USER_COMPOSE)) {
            String userScreenName = getArguments().getString(TweetsAdapter.INTENT_USER_COMPOSE);
            etCompose.setText(userScreenName);
        }

        // Set button click for tweeting
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

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}
