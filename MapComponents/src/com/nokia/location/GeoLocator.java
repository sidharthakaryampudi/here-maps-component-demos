package com.nokia.location;


import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import com.nokia.mid.location.LocationUtil;


/**
 * Abstract Factory Pattern for hiding  a Geolocation device.
 * The Factory will attempt to use  GPS Location and use Cell ID
 * as a fallback.
 *
 */
public class GeoLocator implements Runnable {

    private static final int USE_DEFAULT = -1;
    private LocationProvider provider;
    private LocationListener listener;
    private static GeoLocator locator;
    private Thread t;

    /**
     * I'm a singleton.
     */
    private GeoLocator() {
        provider = createGPSProvider();
        if (provider == null) {
            provider = createCellIdProvider();
        }
    }

    /**
     * Set up the location listener on a separate thread to avoid a delay
     * in returning from the set-up call.
     */
    public void run() {

        provider.setLocationListener(listener, USE_DEFAULT, USE_DEFAULT,
                USE_DEFAULT);
    }

    /**
     * Start locating at a default interval, timeout and maxAge;
     * 
     * @param listener
     *            - an listener who will receive location updates.
     */
    public void setLocationListener(LocationListener listener) {
        if (locator.provider != null) {
            this.listener = listener;
            t = new Thread(this);
            t.start();
        }
    }

    /**
     * Obtain the preferred location device.
     * @return
     */
    public static GeoLocator getInstance() {
        if (locator == null) {
            locator = new GeoLocator();
        }
        return locator;
    }

    /**
     * 
     * @return A Cell ID Location Provider or <code>null</code> if unavailable.
     * 
     */
    private LocationProvider createCellIdProvider() {

        LocationProvider provider = null;
        int[] methods = {
            Location.MTA_ASSISTED | Location.MTE_CELLID
                    | Location.MTY_NETWORKBASED };

        try {
            provider = new PollingCellIdLocator(
                    LocationUtil.getLocationProvider(methods, null));
        } catch (LocationException e) {
            provider = null;
        }
        return provider;
    }

    /**
     * 
     * @return A GPS Location Provider or <code>null</code> if unavailable.
     * 
     */
    private LocationProvider createGPSProvider() {

        LocationProvider provider = null;
        Criteria criteria = new Criteria();

        criteria.setCostAllowed(true);
        criteria.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        criteria.setSpeedAndCourseRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAddressInfoRequired(false);
        try {
            provider = LocationProvider.getInstance(criteria);
        } catch (LocationException e) {
            provider = null;
        }
        return provider;
    }
}
