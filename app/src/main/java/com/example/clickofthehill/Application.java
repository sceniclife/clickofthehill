package com.example.clickofthehill;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Application set-up and tags for logging
 */
public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the application
    public static final String APPTAG = "clickofthehill";

    // Used to pass location from MainActivity to HillActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 25.0f;

    private static SharedPreferences preferences;

    private static ParseConfigHelper configHelper;

    public Application() {

    }

    public static float getSearchDistance() {
        return preferences.getFloat(KEY_SEARCH_DISTANCE, DEFAULT_SEARCH_DISTANCE);
    }

    public static void setSearchDistance(float value) {
        preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
    }

    public static ParseConfigHelper getParseConfigHelper() {
        return configHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Connect to Parse
        ParseObject.registerSubclass(ClickPost.class);
        Parse.initialize(this,
                "OoXhAx95VlWL9bOQjcb8l3HSyi6dCvqFZvrtMkB0",
                "2RvP6KEub5vnYYE38dk7IfHYefKVIOBpbkuCvmMu");

        preferences = getSharedPreferences("com.example.clickofthehill", Context.MODE_PRIVATE);

        configHelper = new ParseConfigHelper();
        configHelper.fetchConfigIfNeeded();
    }

}