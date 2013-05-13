package com.nokia.maps.example.twitter;


import com.nokia.maps.common.GeoCoordinate;
import javax.microedition.lcdui.*;


/**
 * Settings for twitter application
 */
public class TwitterConfig extends Form {

    final static Command OK = new Command("Ok", Command.OK, 1);
    private TextField observe = new TextField("Observe:", "", 200, TextField.ANY);
    private TextField location = new TextField("tweets around:",
            "Berlin", 200, TextField.ANY);
    private TextField radius = new TextField("within a radius (km):", "25", 200,
            TextField.NUMERIC);
    private TextField maxCount = new TextField("Showing at most:", "10", 20,
            TextField.NUMERIC);
    private ChoiceGroup updater = new ChoiceGroup("tweets. Refresh auto.",
            Choice.EXCLUSIVE, new String[] { "yes", "no" }, null);
    private TextField updateInterval = new TextField("every (s)", "60", 80,
            TextField.NUMERIC);
    private GeoCoordinate position; // resolved location coordinates
    private int updateIntervalIndex = -1;

    /**
     * Default constructor.
     */
    public TwitterConfig() {
        super("Settings");
        append(observe);
        append(location);
        append(radius);
        append(maxCount);
        updater.setSelectedIndex(1, true);
        append(updater);
        setItemStateListener(new IntervalModeSwitcher());
        addCommand(OK);
    }

    /**
     * Gets the radius to look for.
     * @return the radius in metres.
     */
    public int getRadius() {
        return Integer.parseInt(radius.getString());
    }

    /**
     * Gets the query string to find on the Twitter feed
     * @return the query string.
     */
    public String getQuery() {
        return observe.getString();
    }

    /**
     * Gets the location to look for.
     * @return the named location to centre the request on.
     */
    public String getLocationString() {
        return location.getString();
    }

    /**
     * Gets the position to make the request around
     * @return a geocoordinate expressing the long/lat to centre the API
     * request upon.
     */
    public GeoCoordinate getPosition() {
        return position;
    }

    /**
     * Sets the position to center the request upon.
     * @param position  the long/lat to centre the API
     * request upon.
     */
    public void setPosition(GeoCoordinate position) {
        this.position = position;
    }

    /**
     *
     * @return maximum number of tweets to display.
     */
    public int getMaxCount() {
        return Integer.parseInt(maxCount.getString());
    }

    /**
     *
     * @return rate of refresh of the app.
     */
    public int getUpdateInterval() {
        if (updater.getSelectedIndex() == 1) {
            return -1;
        }
        return Integer.parseInt(updateInterval.getString());
    }

    /**
     * enables/disables update interval text field
     */
    class IntervalModeSwitcher implements ItemStateListener {

        public void itemStateChanged(Item arg0) {
            if (updater.getSelectedIndex() == 0 && updateIntervalIndex == -1) {
                updateIntervalIndex = append(updateInterval);
            } else if (updater.getSelectedIndex() == 1
                    && updateIntervalIndex != -1) {
                delete(updateIntervalIndex);
                updateIntervalIndex = -1;
            }
        }
    }

    /**
     * @return true if logging is enabled
     */
    public static boolean isLogOn() {
        return false;
    }
}
