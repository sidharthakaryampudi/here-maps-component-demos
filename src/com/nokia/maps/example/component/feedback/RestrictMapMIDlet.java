/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.feedback;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.RestrictMapComponent;
import com.nokia.maps.component.feedback.RestrictMapEventListener;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;
import javax.microedition.lcdui.AlertType;


/**
 * This MIDlet just sets the Restricted Area demo as current Displayable.
 */
public class RestrictMapMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new RestrictMapExample(display, this);

    }

    protected String getTitle() {
        return "Map Restriction Component";
    }

    protected String getDescription() {
        return "Demonstrates the use of the Area restriction Component. This prevents the user "
                + "from panning/zooming outside of a chosen area.";
    }

    /**
     * Demonstrates the use of the Area restriction Component. This prevents the
     * user from panning/zooming outside of a chosen area.
     */
    private class RestrictMapExample extends MapCanvasExample implements RestrictMapEventListener {

        private final RestrictMapComponent restrict;
        private final GeoBoundingBox EUROPE_ONLY = new GeoBoundingBox(
                new GeoCoordinate(65, -10, 0), new GeoCoordinate(35, 45, 0));

        public RestrictMapExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Create the restriction component
            restrict = new RestrictMapComponent(this, this, EUROPE_ONLY);
            // Adding the component to the map will enforce the restriction.
            // Therefore the canvas will display a map of Europe.
            map.addMapComponent(restrict);

        }

        /**
         * Callback when the user attempts to move the map out of the
         * restricted area.
         */
        public void onInvalidMapState() {
            // The current state of the map is invalid,
            // Play an audible warning.
            AlertType.WARNING.playSound(display);
        }
    }
}
