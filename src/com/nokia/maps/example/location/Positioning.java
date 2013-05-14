/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.location;


import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.button.ImageButton;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapCircle;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapStandardMarker;


/**
 *
 * Classic "Where-am-I" button. Calls Device Geolocator and displays it on the map.
 *
 */
public class Positioning extends ImageButton implements
        LocationListener {

    /**
     * Unique ID for the Positioning Component.
     */
    public static final String ID = "Positioning";
    private static final float THRESHOLD_DISTANCE = 100f;
    private final Image locatorOn;
    private final Image locatorOff;
    private boolean locating;
    private final MapStandardMarker youAreHereMarker;
    private final MapCircle youAreHereUncertainty;
    private final MapCanvas mapCanvas;
    private final GeoCoordinate currentLocation = new GeoCoordinate(0, 0, 0);

    /**
     * Constructor for the Location Finder
     *
     *
     * @param mapCanvas the MapCanvas to update when a position is received.
     * @throws IOException if no Button Glyphs are found.
     */
    public Positioning(MapCanvas mapCanvas) throws IOException {
        super(ID, Graphics.TOP | Graphics.RIGHT,
                Image.createImage("/component/button/locate_off.png"),
                Image.createImage("/component/button/locate_e.png"));

        locatorOn = Image.createImage("/component/button/locate_on.png");
        locatorOff = Image.createImage("/component/button/locate_off.png");

        youAreHereMarker = mapCanvas.getMapFactory().createStandardMarker(
                currentLocation, 8, "", MapStandardMarker.HEXAGON);
        youAreHereMarker.setColor(0xAA008000);
        youAreHereUncertainty = mapCanvas.getMapFactory().createMapCircle(100,
                currentLocation);
        youAreHereUncertainty.setColor(0x8000FF00);
        this.mapCanvas = mapCanvas;
    }

    /**
     *  Toggles the geolocator.
     */
    public void toggleButton() {
        locating = !locating;

        setGlyph(locating ? locatorOn : locatorOff);
        if (locating) {
            LocationFinder.getInstance().setLocationListener(this);
        } else {
            LocationFinder.getInstance().setLocationListener(null);
            map.removeMapObject(youAreHereMarker);
            map.removeMapObject(youAreHereUncertainty);
        }
    }

    public void detach(MapDisplay map) {
        map.removeMapObject(youAreHereMarker);
        map.removeMapObject(youAreHereUncertainty);
        super.detach(map);
    }

    /**
     * If a location is received, the map is updated.
     */
    public void locationUpdated(LocationProvider provider, Location location) {

        currentLocation.setLatitude(
                location.getQualifiedCoordinates().getLatitude());
        currentLocation.setLongitude(
                location.getQualifiedCoordinates().getLongitude());
        currentLocation.setAltitude(
                location.getQualifiedCoordinates().getAltitude());

        if (locating) {
            if (map.getCenter().distanceTo(currentLocation) > THRESHOLD_DISTANCE) {
                youAreHereMarker.setCoordinate(currentLocation);

                youAreHereUncertainty.setCenter(currentLocation);
                youAreHereUncertainty.setRadius(
                        location.getQualifiedCoordinates().getHorizontalAccuracy());

                map.removeMapObject(youAreHereMarker);
                map.addMapObject(youAreHereMarker);

                map.removeMapObject(youAreHereUncertainty);
                map.addMapObject(youAreHereUncertainty);

                map.setCenter(currentLocation);
                // Ensure that the Map is refreshed with the new Map State.
                mapCanvas.onMapContentUpdated();
            }
        }

    }

    public void providerStateChanged(LocationProvider arg0, int arg1) {// Do nothing on change of state.
    }
}
