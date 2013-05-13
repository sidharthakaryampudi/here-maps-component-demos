package com.nokia.maps.example.twitter;


import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.common.ServiceError;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapCircle;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.places.Location;
import com.nokia.places.PlaceFactory;
import com.nokia.places.geo.request.GeocodeRequest;


/**
 * Twitter feed map MIDlet
 */
public class TwitterMIDlet extends BaseMIDlet {

    private TwitterConfig config = new TwitterConfig();

    protected MapCanvas getDemo(Display display) {

        return new TwitterDemo(display, this, config);
    }

    protected void startDemo(Display display, MapCanvas demo) {
        // first show configuration displayable
        display.setCurrent(config);
    }

    protected String getTitle() {
        return "Twitter Demo";
    }

    protected String getDescription() {
        return "This example builds up an fully working app which reads JSON data from a webservice";
    }

    /**
     * Main displayable for twitter example
     */
    private class TwitterDemo extends MapCanvasExample implements FocalEventListener {

        private final int MARKER_COLOR = 0xFF43A51B; // color of tweet
        // markers
        private double SURFACE_DISTANCE = 110; // used to estimate
        // distance-to-degrees
        // conversion
        private final Command SHOW_TWEET = new Command("View", Command.OK, 1);
        private final Command EDIT_CONFIG = new Command("Settings", Command.ITEM,
                20);
        private final Command UPDATE_TWEETS = new Command("Update", Command.ITEM,
                10);
        // tweets shown to user [key:marker, value:tweet]
        private Tweet current_tweet; // tweet under cursor or null
        private Timer tweets_updater = new Timer();
        private TwitterRequest twitterRequest = new TwitterRequest();
        private TwitterConfig config;
        private Thread tweetUpdater;
        private Random random = new Random();
        private final FocalObserverComponent addedTweets;
        private final Hashtable tweetAge = new Hashtable();
        private final CenteringComponent centeringComponent;

        /**
         * Creates new canvas and sets itself as command listener for config
         *
         * @param display App display.
         * @param midlet The midlet running the app.
         * @param config the tweet configuration.
         */
        public TwitterDemo(Display display, MIDlet midlet, TwitterConfig config) {
            super(display, midlet);
            this.config = config;
            config.setCommandListener(getCommandListener());
            addCommand(EDIT_CONFIG);

            map.setZoomLevel(15, 0, 0);

            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            } else {
                note("Touch not enabled.", 1500);
            }
            map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

            addedTweets = new FocalObserverComponent(this);
            map.addMapComponent(addedTweets);

            centeringComponent = new CenteringComponent(this,
                    getCommandListener(), SHOW_TWEET);
            map.addMapComponent(centeringComponent);
        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(Command c) {
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
                        "Cannot find location: " + config.getLocationString(),
                        null, AlertType.ERROR);

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
         * @throws IOException on I/O errors
         * @throws ServiceError on service errors
         */
        private void moveToPlaceInSettings() throws IOException, ServiceError {
            PlaceFactory sf = PlaceFactory.getInstance();
            GeocodeRequest geocodeRequest = sf.createGeocodeRequest();
            Location[] locs;

            locs = geocodeRequest.geocode(config.getLocationString(), null);

            if (locs.length > 0 && locs[0].getDisplayPosition() != null) {
                config.setPosition(locs[0].getDisplayPosition());
            }

            MapCircle mc = mapFactory.createMapCircle(config.getRadius() * 1000,
                    config.getPosition());

            map.zoomTo(mc.getBoundingBox(), false);

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
                    Image img;
                    InputStream is = null;

                    try {
                        is = Connector.openInputStream(url);
                        img = Image.createImage(is);
                        a.setImage(img);
                    } catch (Throwable t) {// do not show errors if image
                        // download fails
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
         * @param tweet the tweet data
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
                        error(e.getMessage());
                    }
                    tweetUpdater = null;
                }
            });
            tweetUpdater.start();
        }

        /**
         * Updates Twitter feed
         */
        private void updateTweets()  throws IOException {
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
                        map.pixelToGeo(
                                new Point(map.getWidth(), map.getHeight())));

                addTweet = gbb.contains(gc);
            }
            if (addTweet) {
                MapStandardMarker msm = mapFactory.createStandardMarker(gc, 100,
                        "", MapStandardMarker.BALLOON);

                msm.setColor(MARKER_COLOR);
                map.addMapObject(msm);
                addedTweets.addData(msm, t);
                tweetAge.put(msm, new Long(t.getCreatedTime()));
                repaint();
            }
        }

        /**
         * Clean oldest tweets
         */
        private void cleanOldest() {

            // remove oldest tweets until
            while (tweetAge.size() > config.getMaxCount() - 1) {

                Enumeration t = tweetAge.keys();
                long oldest = Long.MAX_VALUE;
                MapObject oldestKey = null;

                while (t.hasMoreElements()) {
                    Object key = t.nextElement();
                    Long age = (Long) tweetAge.get(key);

                    if (age.longValue() < oldest) {
                        oldest = age.longValue();
                        oldestKey = (MapObject) key;
                    }
                }

                tweetAge.remove(oldestKey);
                addedTweets.removeData(oldestKey);
                map.removeMapObject(oldestKey);
            }
        }

        /**
         * Callback when a Map object is at the centre of the screen
         *
         * @param focus
         *            - the data associated with the focal object.
         */
        public void onFocusChanged(Object focus) {
            current_tweet = (Tweet) focus;

            if (current_tweet != null) {
                addCommand(SHOW_TWEET);
                removeCommand(UPDATE_TWEETS);
                removeCommand(EDIT_CONFIG);
            } else { // no tweet under cursor
                removeCommand(SHOW_TWEET);
                addCommand(UPDATE_TWEETS);
                addCommand(EDIT_CONFIG);
            }
        }
    }
}
