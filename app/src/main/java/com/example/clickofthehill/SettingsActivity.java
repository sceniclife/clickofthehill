package com.example.clickofthehill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.parse.ParseUser;
import com.example.clickofthehill.R;

import java.util.Collections;
import java.util.List;

/**
 * Activity that displays the settings screen.
 */
public class SettingsActivity extends Activity {

    private List<Float> availableOptions = Application.getParseConfigHelper().getSearchDistanceAvailableOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        float currentSearchDistance = Application.getSearchDistance();
        if (!availableOptions.contains(currentSearchDistance)) {
            availableOptions.add(currentSearchDistance);
        }
        Collections.sort(availableOptions);

        // The search distance choices
        RadioGroup searchDistanceRadioGroup = (RadioGroup) findViewById(R.id.searchdistance_radiogroup);

        for (int index = 0; index < availableOptions.size(); index++) {
            float searchDistance = availableOptions.get(index);

            RadioButton button = new RadioButton(this);
            button.setId(index);
            button.setText(getString(R.string.settings_distance_format, (int) searchDistance));
            searchDistanceRadioGroup.addView(button, index);

            if (currentSearchDistance == searchDistance) {
                searchDistanceRadioGroup.check(index);
            }
        }

        // Set up the selection handler to save the selection to the application
        searchDistanceRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Application.setSearchDistance(availableOptions.get(checkedId));
            }
        });
    }

    // Logout Button's click handler
    public void logoutButtonClicked(View view){
        // Call the Parse log out method
        ParseUser.logOut();

        // Re-use the DispatchActivity's logic to route to login screen
        Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
