/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.feedback;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Focal Observer demo as current Displayable.
 */
public class FocalObserverMIDlet extends BaseMIDlet {

    private  final GeoBoundingBox EUROPE = new GeoBoundingBox(
            new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

    protected MapCanvas getDemo(Display display) {
        return new FocalObserverExample(display, this);

    }

    protected String getTitle() {
        return "Focal Observer Component";
    }

    protected String getDescription() {
        return "Demonstrates the use of the focal observer component. This means that when a "
                + "MapObject is moved to the center of the screen, an event is fired in the MapCanvas. "
                + "In this case, the name of the city the marker represents will be displayed in an Alert";
    }

    /**
     * Demonstrates of the focal observer component
     */
    private class FocalObserverExample extends MapCanvasExample implements
            FocalEventListener {

        private final FocalObserverComponent focalObserver;

        public FocalObserverExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            focalObserver = new FocalObserverComponent(this);
            map.addMapComponent(focalObserver);

            addCityMarker(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
            addCityMarker(new GeoCoordinate(51.477811d, -0.001475d, 0), "London");
            addCityMarker(new GeoCoordinate(60.170833, 24.9375, 0), "Helsinki");
            addCityMarker(new GeoCoordinate(59.949444, 10.756389, 0), "Oslo");

            addCityMarker(new GeoCoordinate(45.4375, 12.335833, 0), "Venice");

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

        }

        private void addCityMarker(GeoCoordinate coord, String text) {
            MapStandardMarker marker = mapFactory.createStandardMarker(coord);

            focalObserver.addData(marker, text);
            map.addMapObject(marker);
        }

        /**
         * Callback when a Map object is at the centre of the screen
         *
         * @param focus
         *            - the data associated with the focal object.
         */
        public void onFocusChanged(Object focus) {

            if (focus != null) {
                Alert alertView = new Alert("Focus on ", (String) focus, null,
                        AlertType.ERROR);

                display.setCurrent(alertView);
            }

        }
    }

}
