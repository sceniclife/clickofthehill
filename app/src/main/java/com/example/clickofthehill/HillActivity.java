package com.example.clickofthehill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.example.clickofthehill.R;

import java.util.List;

/**
 * Activity to create and defeat a pre-exising hill
 */
public class HillActivity extends Activity {

    /* Timers
     * StartCountDownTimer does the 3 2 1 GO
     * ClickCountDownTimer does the 60 second timer to click
     */
    StartCountDownTimer startDown;
    ClickCountDownTimer countDown;

    boolean ENABLE = true;
    boolean DISABLE = false;
    boolean STARTED = false;
    boolean FINISHED = false;

    // clickCount keeps track of score
    int clickCount;

    // The post being challenged, new ClickPost if new one is being created
    ClickPost clickPost;

    private int maxCharacterCount = Application.getParseConfigHelper().getPostMaxCharacterCount();
    private ParseGeoPoint geoPoint;

    // UI references.
    private Button clickButton;
    private EditText winMessage;
    private TextView characterCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hill);

        // Get passed Intent Extras
        Intent intent = getIntent();
        Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
        String clickObjectId = intent.getStringExtra("objectId");

        // Initialize CountDownTimers, 4 seconds and 60 seconds, 1 second intervals
        startDown = new StartCountDownTimer(4000, 1000);
        countDown = new ClickCountDownTimer(60000, 1000);

        // New score keeper
        clickCount = 0;

        geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

        // UI references.
        clickButton = (Button) findViewById(R.id.click_button);
        winMessage = (EditText) findViewById(R.id.win_message);
        characterCount = (TextView) findViewById(R.id.character_count);

        // Implement twitter-like character counter
        winMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCharacterCountTextViewText();
                updateClickButtonState();
            }
        });

        // Allow user to start when ready
        updateClickButtonState(ENABLE);

        // New ClickPost, if Challenging a Hill, pass in ObjectId to fetch ClickPost in Parse
        clickPost = new ClickPost();
        updateClickPost(clickObjectId);
    }

    // buttonClicked handler called from the UI
    public void buttonClicked(View view) {
        if (!STARTED) {
            updateClickButtonState(DISABLE);

            Toast.makeText(HillActivity.this, "YOU HAVE 1 MINUTE TO CLICK AS MUCH AS POSSIBLE.", Toast.LENGTH_SHORT)
                    .show();

            startDown.start();

            STARTED = true;
        } else if (!FINISHED) {
            clickButton.setText("" + ++clickCount);
        } else {
            if (clickCount > clickPost.getScore()) {
                post();
            } else {
                finish();
            }
        }
    }

    // Post the new winning score to Parse
    private void post() {
        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(HillActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        // Set the location to the current user's location
        clickPost.setLocation(geoPoint);
        clickPost.setScore(clickCount);
        clickPost.setText(winMessage.getText().toString());
        clickPost.setUser(ParseUser.getCurrentUser());
        ParseACL acl = new ParseACL();

        // Give public read and write access
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        clickPost.setACL(acl);

        // Save the clickPost
        clickPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                finish();
            }
        });
    }

    // Button toggler
    private void updateClickButtonState(boolean toggle) {
        clickButton.setEnabled(toggle);
    }

    // Button toggler when typing win message
    private void updateClickButtonState() {
        int length = winMessage.getText().toString().trim().length();
        boolean toggle = length > 0 && length < maxCharacterCount;
        clickButton.setEnabled(toggle);
    }

    // Twitter-like character counter updater
    private void updateCharacterCountTextViewText() {
        String characterCountString = String.format("%d/%d", winMessage.length(), maxCharacterCount);
        characterCount.setText(characterCountString);
    }

    // Fetch ClickPost to challenge
    private void updateClickPost(String clickObjectId) {
        if (clickObjectId != null) {
            // Get hill data 
            ParseQuery<ClickPost> query = ParseQuery.getQuery("Hills");

            query.whereEqualTo("objectId", clickObjectId);

            query.findInBackground(new FindCallback<ClickPost>() {
                @Override
                public void done(List<ClickPost> list, ParseException e) {
                    if (e == null) {
                        clickPost = list.get(0);
                    }
                }
            });
        }
    }

    // Check score to see if user won, then handle it
    private void checkWin() {
        if (clickCount < clickPost.getScore()) {
            clickButton.setText(getString(R.string.lost_message));
            Toast.makeText(HillActivity.this, "You failed to dethrone, try again.", Toast.LENGTH_LONG)
                    .show();
            updateClickButtonState(ENABLE);
        } else if (clickCount > clickPost.getScore()) {
            // Enable the Win Message box
            winMessage.setVisibility(View.VISIBLE);
            characterCount.setVisibility(View.VISIBLE);
            clickButton.setText(getString(R.string.post_message));
            winMessage.setHint("SUCCESS! YOU GOT: " + clickCount + " - Post a message about your win!");
        } else {
            clickButton.setText(getString(R.string.tie_message));
            Toast.makeText(HillActivity.this, "You tied, try again.", Toast.LENGTH_LONG)
                    .show();
            updateClickButtonState(ENABLE);
        }
    }

    // The Game Timer
    private class ClickCountDownTimer extends CountDownTimer {
        public ClickCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            updateClickButtonState(DISABLE);
            FINISHED = true;
            checkWin();
        }

        @Override
        public void onTick(long milliseconds) {
            //do nothing
        }
    }

    private class StartCountDownTimer extends CountDownTimer {
        public StartCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            updateClickButtonState(ENABLE);
            Toast.makeText(HillActivity.this, "GO!", Toast.LENGTH_SHORT)
                    .show();
            clickButton.setText("0");
            countDown.start();
        }

        @Override
        public void onTick(long milliseconds) {
            clickButton.setText("" + milliseconds / 1000 + "!");
        }
    }
}

