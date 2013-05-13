package com.nokia.maps.example.location;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapCircle;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.mid.location.LocationUtil;
import javax.microedition.lcdui.Graphics;


/**
 * This MIDlet just locates the device using the Cell ID positioning from the
 * Location API.
 */
public class LocationMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new LocationExample(display, this);

    }

    protected String getTitle() {
        return "Location API Example";
    }

    protected String getDescription() {
        return "Demonstrates how to make a request to the location API "
                + "and display the location of the device on a map. ";
    }

    /**
     * Demonstrates how to make a request to the location API
     * using Cell ID positioning and display the location of the device on a map.
     */
    private class LocationExample extends MapCanvasExample implements LocationListener {

        private LocationProvider cellIdLocator;
        private final Command REQUEST_LOCATION = new Command("Locate device",
                Command.ITEM, 1);
        private final ButtonCommand locateButton;
        private final MapStandardMarker marker;
        private final MapCircle uncertainty;
        private final GeoCoordinate currentLocation = new GeoCoordinate(0, 0, 0);
        private static final float THRESHOLD_DISTANCE = 100f;
        private static final int DEFAULT_TIMEOUT = -1;
        private Thread t;
        
        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the location Demo.
         *
         * @param display
         * @param midlet
         */
        public LocationExample(Display display, MIDlet midlet) {
            super(display, midlet);

            marker = getMapFactory().createStandardMarker(currentLocation, 8, "",
                    MapStandardMarker.HEXAGON);
            marker.setColor(0xAA008000);
            uncertainty = getMapFactory().createMapCircle(100, currentLocation);
            uncertainty.setColor(0x8000FF00);

            // Set up the map show the whole of the European Continent.          
            map.zoomTo(EUROPE, false);
            locateButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT, this,
                    getCommandListener(), REQUEST_LOCATION);
            map.addMapComponent(locateButton);

        }

        /**
         * This thread creates the cellIdLocator and makes the
         * positioning request.
         */

        /**
         * Standard Command button interaction.
         *
         * @param c
         *
         */
        public void commandRun(final Command c) {
            if (c == REQUEST_LOCATION) {
                // Start the request on a separate thread
                // as it may take some time.
                t = new Thread(
                        new Runnable() {

                    public void run() {
                        try {
                            progressStart("Locating",
                                    "Location  Request Failed.");
                            cellIdLocator = getCellIdProvider();
                            locationUpdated(cellIdLocator,
                                    cellIdLocator.getLocation(DEFAULT_TIMEOUT));
                        } catch (InterruptedException e) {
                            error("Location request timed out.");
                        } catch (LocationException e) {
                            error("Location API not available.");
                        } catch (NoClassDefFoundError e) {
                            error("Location API not available.");
                        }
                    }
                });
                t.start();
            }
        }

        /**
         * If a location is received, the map is updated.
         */
        public void locationUpdated(LocationProvider provider, Location location) {

            progressEnd();

            currentLocation.setLatitude(
                    location.getQualifiedCoordinates().getLatitude());
            currentLocation.setLongitude(
                    location.getQualifiedCoordinates().getLongitude());
            currentLocation.setAltitude(
                    location.getQualifiedCoordinates().getAltitude());

            if (map.getCenter().distanceTo(currentLocation) > THRESHOLD_DISTANCE) {
                marker.setCoordinate(currentLocation);

                uncertainty.setCenter(currentLocation);
                uncertainty.setRadius(
                        location.getQualifiedCoordinates().getHorizontalAccuracy());

                map.removeMapObject(marker);
                map.addMapObject(marker);

                map.removeMapObject(uncertainty);
                map.addMapObject(uncertainty);

                map.setCenter(currentLocation);
                // Ensure that the Map is refreshed with the new Map State.
                onMapContentUpdated();
            }

        }

        public void providerStateChanged(LocationProvider arg0, int arg1) {// Do nothing on change of state.
        }

        /**
         *
         * @return A Cell ID Location Provider or <code>null</code> if
         *         unavailable.
         * @throws LocationException if no provider available.
         *
         */
        private LocationProvider getCellIdProvider() throws LocationException {
            if (cellIdLocator == null) {
                int[] methods = {
                    Location.MTA_ASSISTED | Location.MTE_CELLID
                            | Location.MTY_NETWORKBASED};

                cellIdLocator = LocationUtil.getLocationProvider(methods, null);
            }
            return cellIdLocator;
        }
    }
}
