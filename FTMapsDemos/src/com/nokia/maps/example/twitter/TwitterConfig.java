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
            "London, Britain", 200, TextField.ANY);
    private TextField radius = new TextField("within a radius (km):", "25", 200,
            TextField.NUMERIC);
    private TextField maxCount = new TextField("Showing at most:", "99", 20,
            TextField.NUMERIC);
    private ChoiceGroup updater = new ChoiceGroup("tweets. Refresh auto.",
            Choice.EXCLUSIVE, new String[] { "yes", "no"}, null);
    private TextField updateInterval = new TextField("every (s)", "60", 80,
            TextField.NUMERIC);
    private GeoCoordinate position; // resolved location coordinates
    private int updateIntervalIndex = -1;

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

    public int getRadius() {
        return Integer.parseInt(radius.getString());
    }

    public String getQuery() {
        return observe.getString();
    }

    public String getLocationString() {
        return location.getString();
    }

    public GeoCoordinate getPosition() {
        return position;
    }

    public void setPosition(GeoCoordinate position) {
        this.position = position;
    }

    public int getMaxCount() {
        return Integer.parseInt(maxCount.getString());
    }

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
