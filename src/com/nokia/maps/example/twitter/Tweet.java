package com.nokia.maps.example.twitter;


import java.util.Hashtable;

import com.nokia.maps.common.GeoCoordinate;


/**
 * Twitter tweet data with created time.
 */
class Tweet extends Hashtable {

    private String text;
    private String location;
    private String profileImageUrl;
    private String fromUser;
    private GeoCoordinate userCoordinate;
    private long createdTime;

    /**
     * Constructs new tweet and sets created time.
     *
     * @param text the text of the teet.
     * @param location the name of the location of the tweet.
     * @param profile_image_url - user pciture
     * @param fromUser user name
     * @param gc the location as  a Geocoordinate.
     */
    public Tweet(String text, String location, String profile_image_url,
            String fromUser, GeoCoordinate gc) {
        this.text = text;
        this.location = location;
        this.profileImageUrl = profile_image_url;
        this.fromUser = fromUser;
        this.userCoordinate = gc;
        createdTime = System.currentTimeMillis();
    }

    /**
     * Returns the text
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the location
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the profileImageUrl
     * @return the profileImageUrl
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Returns the fromUser
     * @return the fromUser
     */
    public String getFromUser() {
        return fromUser;
    }

    /**
     * Returns the userCoordinate
     * @return the userCoordinate
     */
    public GeoCoordinate getUserCoordinate() {
        return userCoordinate;
    }

    /**
     * Returns the createdTime
     * @return the createdTime
     */
    public long getCreatedTime() {
        return createdTime;
    }

    /**
     * @param coordinate the coordinate to set
     */
    public void setUserCoordinate(GeoCoordinate coordinate) {
        this.userCoordinate = coordinate;
    }
}
