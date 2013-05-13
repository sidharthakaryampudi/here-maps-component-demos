package com.nokia.maps.example.component.touch;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.InfoBubbleComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the inforbubble demo as current Displayable.
 */
public class InfoBubbleMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new InfoBubbleExample(display, this);

    }

    protected String getTitle() {
        return "Infobubble Component";
    }

    protected String getDescription() {
        return "Demonstrates adding a series of Infobubbles to the map. When a Map "
                + "Marker is centered on the screen,  it is replaced by an Infobubble. Unlike a "
                + " tooltip, an infobubble is itself clickable, and can also be scrolled if "
                + "the text is too long.";
    }

    /**
     * Demonstrates adding a series of Infobubbles to the map. When a Map Marker
     * is centered on the screen, it is replaced by an Infobubble. Unlike a
     * tooltip, an infobubble is itself clickable, and can also be scrolled if
     * the text is too long.
     */
    private class InfoBubbleExample extends GestureMapCanvasExample implements FocalEventListener {

        private final Command BUBBLE = new Command("InfoBubble", Command.OK, 1);

        private final InfoBubbleComponent infoBubble;
        private final FocalObserverComponent focalComponent;
        private final Alert alert;

        private String currentFocus;

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Infobubble Demo.
         *
         * @param display
         * @param midlet
         */

        public InfoBubbleExample(Display display, MIDlet midlet) {
            super(display, midlet);

            alert = new Alert("");
            alert.setTimeout(1000);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            map.addMapComponent(new CenteringComponent(this));

            // Add the infobubble component FIRST so that it is processed LAST.
            infoBubble = new InfoBubbleComponent(this, getCommandListener());
            map.addMapComponent(infoBubble);

            // Add the Focal component SECOND to feed the info bubble component
            // ABOVE.
            focalComponent = new FocalObserverComponent(this);
            map.addMapComponent(focalComponent);

            // Finally add the Centering Component to feed the focal observer
            map.addMapComponent(new CenteringComponent(this));

            // Now we can set up the markers..
            addMarkerData(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
            addMarkerData(new GeoCoordinate(51.477811d, -0.001475d, 0),
                    "London, this is the city where the Olympic Games took place in the summer of 2012.\n\n"
                    + "The 2012 Summer Olympics, officially the Games of the XXX Olympiad, and also more generally known as London 2012, was a major international"
                    + " multi-sport event, celebrated in the tradition of the Olympic Games, as governed by the International Olympic Committee (IOC), that took place in"
                    + " London, United Kingdom, from 27 July to 12 August 2012. The first event, the group stages in women's football, began two days earlier, on 25 July."
                    + " More than 10,000 athletes from 204 National Olympic Committees (NOCs) participated.\n\n"
                    + "Following a bid headed by former Olympic champion Sebastian Coe and then-Mayor of London Ken Livingstone, London was selected as the host city on "
                    + "6 July 2005 during the 117th IOC Session in Singapore, defeating bids from Moscow, New York City, Madrid and Paris."
                    + "London was the first city to officially host the modern Olympic Games three times, having previously done so in 1908 and in 1948.");
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
         * The callback from the focal observer sets up the Infobubble.
         */
        public void onFocusChanged(Object focus) {

            currentFocus = (String) focus;
            if (currentFocus != null) {
                infoBubble.addData(currentFocus, BUBBLE);
            } else {
                infoBubble.clear();
            }
        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         * @param d
         */
        public void commandRun(final Command c) {
            if (c == BUBBLE) {
                // The infobubble has been clicked.
                alert.setString(
                        currentFocus + " " + c.getLabel() + " was pressed");
                display.setCurrent(alert);
            }
        }
    }

}
