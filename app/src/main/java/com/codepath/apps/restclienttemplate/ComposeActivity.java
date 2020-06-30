package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;

public class ComposeActivity extends AppCompatActivity {

    public static final int MAX_TWEET_LENGTH = 140;
    EditText etCompose;
    com.google.android.material.textfield.TextInputLayout textInputLayout;
    Button btnTweet;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        context = this;
        etCompose = findViewById(R.id.etCompose);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);
        btnTweet = findViewById(R.id.btnTweet);

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
                Toast.makeText(context, tweetContent,
                        Toast.LENGTH_LONG).show();

            }
            // Make an API call to Twitter to publish the tweet
            
        });
    }
}
