/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.feedback;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Tooltip demo as current Displayable.
 */
public class TooltipMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new TooltipExample(display, this);

    }

    protected String getTitle() {
        return "Tooltip Component";
    }

    protected String getDescription() {
        return "Demonstrates adding a series of tooltips to map markers. When a Map "
                + "Marker is centered on the screen, a tooltip appears below it. Unlike "
                + "Infobubbles, the tooltips do not react to Touch Events.";
    }

    /**
     * Demonstrates adding a series of tooltips to map markers. When a Map
     * Marker is centered on the screen, a tooltip appears below it. Unlike
     * Infobubbles, the tooltips do not react to Touch Events.
     */
    private class TooltipExample extends MapCanvasExample implements FocalEventListener {

        private final TooltipComponent tooltips;
        private final FocalObserverComponent focalComponent;
        private  final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Tooltip Demo.
         *
         * @param display
         * @param midlet
         */
        public TooltipExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            map.addMapComponent(new CenteringComponent(this));

            // Add the tooltip component FIRST so that it is processed LAST.
            tooltips = new TooltipComponent(this);
            map.addMapComponent(tooltips);

            // Add the Focal component SECOND to
            focalComponent = new FocalObserverComponent(this);
            map.addMapComponent(focalComponent);

            // Now we can set up the markers..
            addMarkerData(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
            addMarkerData(new GeoCoordinate(51.477811d, -0.001475d, 0),
                    "London, this is the city where the Olympic Games took place in the summer of 2012.");
            addMarkerData(new GeoCoordinate(60.170833, 24.9375, 0), "Helsinki");
            addMarkerData(new GeoCoordinate(59.949444, 10.756389, 0), "Oslo");

            addMarkerData(new GeoCoordinate(45.4375, 12.335833, 0), "Venice");

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

        }

        /**
         * Helper function to add markers and prime the Focal Observer with
         * data.
         *
         * @param coord
         * @param text
         */
        private void addMarkerData(GeoCoordinate coord, String text) {
            MapStandardMarker marker = mapFactory.createStandardMarker(coord);

            focalComponent.addData(marker, text);

            map.addMapObject(marker);
        }

        /**
         * The callback from the focal observer sets up the Tooltip text.
         */
        public void onFocusChanged(Object focus) {

            String currentFocus = (String) focus;

            if (currentFocus != null) {
                tooltips.add(currentFocus);
            } else {
                tooltips.clear();
            }
        }
    }

}
