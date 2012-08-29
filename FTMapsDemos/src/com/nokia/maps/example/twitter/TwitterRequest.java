package com.nokia.maps.example.twitter;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import com.nokia.maps.example.twitter.json.JSONArray;
import com.nokia.maps.example.twitter.json.JSONObject;
import com.nokia.maps.example.twitter.json.JSONParser;
import com.nokia.maps.common.GeoCoordinate;


/**
 * Class for accessing Twitter search service
 */
class TwitterRequest {

    private static final String SERVER = "http://search.twitter.com/search.json?";
    // value names in json response
    private static final String KEY_TEXT = "text";
    private static final String KEY_IMAGE = "profile_image_url";
    private static final String KEY_GEO = "geo";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_COORDINATES = "coordinates";
    private static final Object KEY_USER = "from_user";
    private static final Object KEY_RESULTS = "results";
    private static final String KEY_REFRESH = "refresh_url";
    // feed can be updated using refresh obtained from twitter response
    private String refresh = null;

    /**
     * Updates feed and parses response
     */
    public Vector getTweets(GeoCoordinate coord, int km, String query, int count) throws IOException {
        Vector tweets = new Vector();
        JSONObject obj = JSONParser.parse(createUrl(coord, km, query, count));
        JSONArray results = (JSONArray) obj.get(KEY_RESULTS);
        Enumeration resultsEnum = results.elements();

        // create Tweet objects from every field in results array
        while (resultsEnum.hasMoreElements()) {
            JSONObject t = (JSONObject) resultsEnum.nextElement();
            String user = (String) t.get(KEY_USER);
            String profile_image_url = (String) t.get(KEY_IMAGE);
            String location = (String) t.get(KEY_LOCATION);
            String text = (String) t.get(KEY_TEXT);
            GeoCoordinate gc = null;
            Object geoTemp = t.get(KEY_GEO);

            // Tweet may have exact location in geo element
            if (geoTemp != null && geoTemp instanceof JSONObject) {
                JSONObject geo = (JSONObject) geoTemp;
                // tweet has exact location
                Vector values = ((JSONArray) geo.get(KEY_COORDINATES));

                gc = new GeoCoordinate(
                        Double.parseDouble((String) values.elementAt(0)),
                        Double.parseDouble((String) values.elementAt(1)), 0);
            }
            tweets.addElement(
                    new Tweet(text, location, profile_image_url, user, gc));
        }
        refresh = (String) obj.get(KEY_REFRESH);
        return tweets;
    }

    /**
     * stop refreshing old query
     */
    public void reset() {
        refresh = null;
    }

    /**
     * Creates URL for Twitter search service.
     */
    private String createUrl(GeoCoordinate coord, int km, String query,
            int count) {
        StringBuffer sb = new StringBuffer();

        sb.append(SERVER);
        if (refresh != null) {
            sb.append(refresh);
            refresh = null;
        } else {
            sb.append("q=" + urlencode(query));
        }
        sb.append("&lang=en");
        sb.append("&rpp=" + count);
        sb.append(
                "&geocode=" + +coord.getLatitude() + "," + coord.getLongitude()
                + "," + km + "km");
        return sb.toString();
    }

    /**
     * Encode url
     */
    private String urlencode(String query) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < query.length(); i++) {
            char ch = query.charAt(i);

            if (Character.isDigit(ch) || Character.isLowerCase(ch)
                    || Character.isUpperCase(ch)) {
                sb.append(ch);
            } else {
                if (ch < 15) {
                    sb.append("%0" + Integer.toHexString(ch).toUpperCase());
                } else {
                    sb.append("%" + Integer.toHexString(ch).toUpperCase());
                }
            }
        }
        return sb.toString();
    }
}
