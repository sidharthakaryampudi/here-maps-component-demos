package com.nokia.maps.example.location;


import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;


/**
 *
 * Wrapper around the CellId Location Provider so we can poll for location.
 *
 */
public class PollingCellIdLocator extends LocationProvider {

    private TimerTask task;
    private final Timer timer;
    private final LocationProvider cellIdLocator;
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int POLLING_PERIOD = 10000;

    /**
     * Constructor - sets up the polling Timer.
     * @param cellIdLocator
     */
    public PollingCellIdLocator(LocationProvider cellIdLocator) {
        timer = new Timer();
        this.cellIdLocator = cellIdLocator;
    }

    /**
     *
     * This mimics the locationUpdated interface.
     *
     */
    private class CellIdLocationPollingTask extends TimerTask {

        private final LocationProvider cellIdLocator;
        private final LocationListener listener;

        protected CellIdLocationPollingTask(LocationProvider cellIdLocator,
                LocationListener listener) {
            this.cellIdLocator = cellIdLocator;
            this.listener = listener;
        }

        public void run() {
            try {
                listener.locationUpdated(cellIdLocator,
                        cellIdLocator.getLocation(DEFAULT_TIMEOUT));
            } catch (InterruptedException e) {// Don't update.
            } catch (LocationException e) {// Don't update.
            }
        }
    }

    /**
     * Starts checking for locations.
     */
    public Location getLocation(int timeout) throws LocationException,
                InterruptedException {
        return cellIdLocator.getLocation(timeout);
    }

    /**
     * Returns the state of the locator.
     */
    public int getState() {
        return cellIdLocator.getState();
    }

    /**
     * Resets the locator.
     */
    public void reset() {
        cellIdLocator.reset();
    }

    /**
     * Registers a Location Listener.
     */
    public void setLocationListener(LocationListener listener, int interval,
            int timeout, int maxAge) {

        if (listener == null) {
            if (task != null) {
                task.cancel();
                task = null;
            }
        } else {
            task = new CellIdLocationPollingTask(cellIdLocator, listener);
            timer.scheduleAtFixedRate(task, 0, POLLING_PERIOD);
        }

    }

}
