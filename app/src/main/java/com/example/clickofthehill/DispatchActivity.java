package com.example.clickofthehill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

/**
 * Activity to dispatch whether user is already logged in or needs to log in
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for users that are cached/logged in
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Start and intent for users that need to log in
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

}
