package com.example.clickofthehill;

import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ParseConfigHelper {
    private ParseConfig config;
    private long configLastFetchedTime;

    public void fetchConfigIfNeeded() {
        final long configRefreshInterval = 60 * 60; // 1 hour

        if (config == null || System.currentTimeMillis() - configLastFetchedTime > configRefreshInterval) {
            // Set the config to current, just to load the cache
            config = ParseConfig.getCurrentConfig();

            // Set the current time, to flag that the operation started and prevent double fetch
            ParseConfig.getInBackground(new ConfigCallback() {
                @Override
                public void done(ParseConfig parseConfig, ParseException e) {
                    if (e == null) {
                        // Successfully retrieved
                        config = parseConfig;
                        configLastFetchedTime = System.currentTimeMillis();
                    } else {
                        // Fetch failed, reset the time
                        configLastFetchedTime = 0;
                    }
                }
            });
        }
    }

    public List<Float> getSearchDistanceAvailableOptions() {
        final List<Float> defaultOptions = Arrays.asList(25.0f, 50.0f, 75.0f, 100.0f);

        List<Number> options = config.getList("distanceFilters");
        if (options == null) {
            return defaultOptions;
        }

        List<Float> typedOptions = new ArrayList<Float>();
        for (Number option : options) {
            typedOptions.add(option.floatValue());
        }

        return typedOptions;
    }

    public int getPostMaxCharacterCount() {
        int value = config.getInt("postMaxCharacterCount", 140);

        //reserving 10 for Score suffix
        value = value - 10;
        return value;
    }
}
