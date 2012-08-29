package com.nokia.maps.example.twitter;


import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.common.ServiceError;
import com.nokia.maps.example.Base;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.maps.common.Location;
import com.nokia.maps.search.GeocodeRequest;
import com.nokia.maps.search.SearchFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;


/**
 * Main displayable for twitter example
 */
public class TwitterCanvas extends Base {

    private static final int MARKER_COLOR = 0xFF43A51B; // color of tweet markers
    private static double SURFACE_DISTANCE = 110; // used to estimate distance-to-degrees conversion
    private final Command SHOW_TWEET = new Command("View", Command.OK, 1);
    private final Command EDIT_CONFIG = new Command("Settings", Command.ITEM, 20);
    private final Command UPDATE_TWEETS = new Command("Update", Command.ITEM, 10);
    // tweets shown to user [key:marker, value:tweet]
    private Hashtable added_tweets = new Hashtable();
    private Tweet current_tweet; // tweet under cursor or null
    private Timer tweets_updater = new Timer();
    private TwitterRequest twitterRequest = new TwitterRequest();
    private TwitterConfig config;
    private Thread tweetUpdater;
    private Random random = new Random();

    /**
     * Creates new canvas and sets itself as command listener for config
     */
    public TwitterCanvas(Display display, MIDlet midlet, TwitterConfig config) {
        super(display, midlet);
        this.config = config;
        config.setCommandListener(this);
        addCommand(EDIT_CONFIG);

        map.setZoomLevel(15, 0, 0);
        map.addMapComponent(new Updater());
    }

    /**
     * Called from thread executing the command
     */
    protected void commandRun(Command c) {
        if (c == SHOW_TWEET) {
            showTweet();
        } else if (c == TwitterConfig.OK) {
            showTwitterMap();
        } else if (c == EDIT_CONFIG) {
            showSettings();
        } else if (c == UPDATE_TWEETS) {
            startTweetUpdate();
        }
    }

    /**
     * Cancels pending operations and shown settings displayable
     */
    private void showSettings() {
        twitterRequest.reset();
        if (tweets_updater != null) {
            tweets_updater.cancel();
            tweets_updater = null;
        }
        display.setCurrent(config);
    }

    /**
     * Sets Twitter map as current displayable and starts feed update.
     */
    private void showTwitterMap() {
        try {
            moveToPlaceInSettings();
        } catch (Throwable t) {
            Alert a = new Alert("Failed",
                    "Cannot find location: " + config.getLocationString(), null,
                    AlertType.ERROR);

            a.setTimeout(Alert.FOREVER);
            display.setCurrent(a, config);
            return;
        }
        display.setCurrent(this);
        if (config.getUpdateInterval() <= 0) {
            startTweetUpdate();
        } else { // scheduled mode
            tweets_updater = new Timer();
            tweets_updater.scheduleAtFixedRate(new TweetsUpdater(), 50,
                    config.getUpdateInterval() * 1000);
        }
    }

    /**
     * centers and zoom map according to config
     */
    private void moveToPlaceInSettings() throws IOException, ServiceError {
        SearchFactory sf = SearchFactory.getInstance();
        GeocodeRequest geocodeRequest = sf.createGeocodeRequest();
        Location[] locs;

        locs = geocodeRequest.geocode(config.getLocationString(), null);

        if (locs.length > 0 && locs[0].getDisplayPosition() != null) {
            config.setPosition(locs[0].getDisplayPosition());
        }

        map.setCenter(config.getPosition());

        // set correct zoom
        double radius = config.getRadius();
        double maxDegreeDiff = radius / SURFACE_DISTANCE;
        GeoCoordinate cc = config.getPosition();

        try {
            GeoCoordinate topLeft = new GeoCoordinate(
                    cc.getLatitude() - maxDegreeDiff,
                    cc.getLongitude() - maxDegreeDiff, 0);
            GeoCoordinate bottomRight = new GeoCoordinate(
                    cc.getLatitude() + maxDegreeDiff,
                    cc.getLongitude() + maxDegreeDiff, 0);
            GeoBoundingBox box = new GeoBoundingBox(topLeft, bottomRight);

            map.zoomTo(box, true);
        } catch (Throwable t) {// errors ignored that don't need to check that center position plus
            // maxDegreeDiff fits on map area
        }
    }

    /**
     * Shows current tweet dialog and downloads user image
     */
    private void showTweet() {
        String text = current_tweet.getText();

        if (text != null) {
            String from = current_tweet.getFromUser();
            Alert a = new Alert("" + from, text, null, AlertType.INFO);

            a.setTimeout(Alert.FOREVER);
            display.setCurrent(a, this);
            String url = current_tweet.getProfileImageUrl();

            if (url != null) {
                // download and set user image to visible alert
                log("Create image " + url);
                Image img;
                InputStream is = null;

                try {
                    is = Connector.openInputStream(url);
                    img = Image.createImage(is);
                    a.setImage(img);
                } catch (Throwable t) {// do not show errors if image download fails
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Throwable e) {}
                }
            }
        }
    }

    /**
     * Sets tweet position randomly within radius
     */
    private void randomPlaceWithinRadius(Tweet tweet) {
        double radius = config.getRadius();
        double maxDegreeDiff = radius / SURFACE_DISTANCE;
        double latR = random.nextDouble();
        double lonR = random.nextDouble();

        latR = (latR - 0.5) * 2; // -1 to 1
        lonR = (lonR - 0.5) * 2; // -1 to 1
        GeoCoordinate sc = config.getPosition();
        GeoCoordinate gc = new GeoCoordinate(
                sc.getLatitude() + latR * maxDegreeDiff,
                sc.getLongitude() + lonR * maxDegreeDiff, 0);

        tweet.setUserCoordinate(gc);

    }

    /**
     * task scheduled to feed update thread
     */
    class TweetsUpdater extends TimerTask {

        public void run() {
            log("TweetsUpdater::run+ updater:" + tweetUpdater);
            startTweetUpdate();
        }
    }

    /**
     * Start twitter feed update in separate thread
     */
    private void startTweetUpdate() {
        // let's not start update if settings is shown or update is already
        // active
        if (tweetUpdater != null && display.getCurrent() != this) {
            return;
        }
        tweetUpdater = new Thread(new Runnable() {

            public void run() {
                try {
                    updateTweets();
                } catch (Throwable e) {
                    // ignore all errors, try again later
                    log("tweet update failed");
                    e.printStackTrace();
                }
                tweetUpdater = null;
            }
        });
        tweetUpdater.start();
    }

    /**
     * Updates Twitter feed
     */
    private void updateTweets() throws Throwable {
        Vector tweets = twitterRequest.getTweets(config.getPosition(),
                config.getRadius(), config.getQuery(), config.getMaxCount());

        for (Enumeration iterator = tweets.elements(); iterator.hasMoreElements();) {
            Tweet tweet = (Tweet) iterator.nextElement();

            if (tweet.getUserCoordinate() == null) {
                randomPlaceWithinRadius(tweet);
            }
            addTweet(tweet);
        }
    }

    /**
     * creates marker for tweet and its to map
     */
    private void addTweet(Tweet t) {
        if (t.getText() == null) {
            // ignore tweets without text
            return;
        }
        GeoCoordinate gc = t.getUserCoordinate();
        boolean addTweet = true;

        cleanOldest();
        if (map.getZoomLevel() > 0) {
            GeoBoundingBox gbb = new GeoBoundingBox(
                    map.pixelToGeo(new Point(0, 0)),
                    map.pixelToGeo(new Point(map.getWidth(), map.getHeight())));

            addTweet = gbb.contains(gc);
        }
        if (addTweet) {
            MapStandardMarker msm = mapFactory.createStandardMarker(gc, 100, "",
                    MapStandardMarker.BALLOON);

            msm.setColor(MARKER_COLOR);
            map.addMapObject(msm);
            added_tweets.put(msm, t);
            repaint();
        }
    }

    /**
     * Clean oldest tweets
     */
    private void cleanOldest() {
        log("cleanTweets count " + added_tweets.size());

        // remove oldest tweets until
        while (added_tweets.size() > config.getMaxCount() - 1) {

            Enumeration t = added_tweets.keys();
            long oldest = Long.MAX_VALUE;
            Object oldestKey = null;

            while (t.hasMoreElements()) {
                Object key = t.nextElement();
                Tweet te = (Tweet) added_tweets.get(key);

                if (te.getCreatedTime() < oldest) {
                    oldest = te.getCreatedTime();
                    oldestKey = key;
                }
            }
            log(
                    "cleanTweets remove: "
                            + ((Tweet) added_tweets.get(oldestKey)).getText());
            added_tweets.remove(oldestKey);
            map.removeMapObject((MapObject) oldestKey);
        }
    }

    /**
     * Checks if cursor is above tweet and updates ui accordingly
     */
    class Updater extends MapComponentImpl {

        Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);

        public void mapUpdated(boolean zoomChanged) {
            MapObject mo = map.getObjectAt(center);

            if (mo != null && mo instanceof MapStandardMarker) {
                MapStandardMarker msm = (MapStandardMarker) mo;

                // By adding and removing we are changing the order
                // so that this object appears on top of the others.
                map.removeMapObject(mo);
                map.addMapObject(mo);
                current_tweet = (Tweet) added_tweets.get(msm);
                addCommand(SHOW_TWEET);
                removeCommand(UPDATE_TWEETS);
                removeCommand(EDIT_CONFIG);
            } else { // no tweet under cursor
                current_tweet = null;
                removeCommand(SHOW_TWEET);
                addCommand(UPDATE_TWEETS);
                addCommand(EDIT_CONFIG);
            }
        }
    }

    /**
     * Overrides method from canvas to handle fire key
     */
    protected void keyReleased(int key) {
        if (getGameAction(key) == Canvas.FIRE && current_tweet != null) {
            commandAction(SHOW_TWEET, this);
        }
        super.keyReleased(key);
    }

    /**
     * For debugging
     */
    void log(String string) {
        if (TwitterConfig.isLogOn()) {
            System.out.println(string);
        }
    }
}
