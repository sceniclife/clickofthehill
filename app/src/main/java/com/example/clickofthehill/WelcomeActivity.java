package com.example.clickofthehill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.clickofthehill.R;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void loginButtonClick(View view) {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }

    public void signUpButtonClick(View view) {
        startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
    }
}
