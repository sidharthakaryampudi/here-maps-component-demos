/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.AbstractMapComponent;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.Point;


/**
 * Example of a minimal Custom MapComponent responding to touch. This MapComponent
 * alert the user when the map has been touched on the screen.
 */
public class PixelToGeoComponent extends AbstractMapComponent implements EventListener {

    /**
     * Unique ID for the Touch Location Component.
     */
    public static final String ID = "TouchLocator";
    private static final String VERSION = "1.0";
    private boolean componentActivated;
    private final Display display;

    /**
     * Default constructor.
     * @param display the display to use when alerting the user.
     */
    public PixelToGeoComponent(Display display) {
        super(ID, VERSION);
        this.display = display;
    }

    public EventListener getEventListener() {
        return this;
    }

    private void displayLocationPressed(Point point) {
        GeoCoordinate touchedAt = map.pixelToGeo(point);
        double lat = (Math.floor(touchedAt.getLatitude() * 10)) / 10d;
        double lng = (Math.floor(touchedAt.getLongitude() * 10)) / 10d;
        Alert alertView = new Alert("Touch",
                "Touched at:" + Math.abs(lat) + ((lat > 0) ? "N" : "S") + " "
                + Math.abs(lng) + ((lng > 0) ? "E" : "W"),
                null,
                AlertType.ERROR);

        display.setCurrent(alertView);
    }

    public boolean pointerDragged(int x, int y) {
        // If the user presses down and drags the pointer,
        // Do not respond on a subsequent release
        componentActivated = false;
        // Allow other components (such as dragging the map)
        // to handle this event after this component.
        return false;
    }

    public boolean pointerPressed(int x, int y) {
        componentActivated = true;
        // Allow other components (such the Zoom Image Control)
        // to handle this event after this component.
        return false;
    }

    public boolean pointerReleased(int x, int y) {
        if (componentActivated) {
            displayLocationPressed(new Point(x, y));
        }
        // Stop further processing if the component has fired.
        return componentActivated;
    }

    /**
     * Called when a key is pressed.
     *
     * @param keyCode the key code
     * @param gameAction the gameAction
     * @return true if key was consumed
     */
    public boolean keyPressed(int keyCode, int gameAction) {
        return false;
    }

    /**
     * Called when a key is released.
     *
     * @param keyCode
     *            the key code
     * @param gameAction the gameAction
     * @return true if key was consumed
     */
    public boolean keyReleased(int keyCode, int gameAction) {
        return false;
    }

    /**
     * Called when a key is repeated.
     *
     * @param keyCode
     *            the key code
     * @param gameAction the gameAction
     * @param repeatCount the repeat count
     * @return true if key was consumed
     */
    public boolean keyRepeated(int keyCode, int gameAction, int repeatCount) {
        return false;
    }
}
