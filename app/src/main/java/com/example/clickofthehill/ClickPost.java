package com.example.clickofthehill;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Data model for a hill. Table is set up in Parse via the ParseClassName
 */
@ParseClassName("Hills")
public class ClickPost extends ParseObject {
    public static ParseQuery<ClickPost> getQuery() {
        return ParseQuery.getQuery(ClickPost.class);
    }

    public int getScore() {
        return getInt("score");
    }

    public void setScore(int value) {
        put("score", value);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }
}
