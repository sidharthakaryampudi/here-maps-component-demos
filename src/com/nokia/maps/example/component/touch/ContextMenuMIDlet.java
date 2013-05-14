/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.touch;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.ContextMenuComponent;

import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Context Menu demo as current Displayable.
 */
public class ContextMenuMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new ContextMenuExample(display, this);

    }

    protected String getTitle() {
        return "Context Menu Component";
    }

    protected String getDescription() {
        return "Demonstrates the use of context menus on the map. When a Map "
                + "Marker is centered on the screen, a context menu appears below it. The "
                + "menu is clickable and scrollable.";
    }

    /**
     * Demonstrates adding a series of Context Menus to the map. When a Map
     * Marker is centered on the screen, a context menu appears below it. The
     * menu is clickable and scrollable.
     */
    private class ContextMenuExample extends MapCanvasExample implements FocalEventListener {

        private final ContextMenuComponent contextMenus;
        private final FocalObserverComponent focalComponent;

        /**
         * Set up a series of commands for the Context menus to use.
         */
        private final Command DO1 = new Command("item 1", Command.ITEM, 1);
        private final Command DO2 = new Command("item 2", Command.ITEM, 2);
        private final Command DO3 = new Command("item 3", Command.ITEM, 3);
        private final Command DO4 = new Command("item 4", Command.ITEM, 4);
        private final Command DO5 = new Command("item 5", Command.ITEM, 5);
        private final Command DO6 = new Command("item 6", Command.ITEM, 6);
        private final Command DO7 = new Command("item 7", Command.ITEM, 7);
        private final Command DO8 = new Command("item 8", Command.ITEM, 8);
        private final Command[] commands = {
            DO1, DO2, DO3, DO4, DO5, DO6, DO7, DO8 };

        private final Alert alert;
        private String currentFocus;

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Context Menu Demo.
         *
         * @param display
         * @param midlet
         */
        public ContextMenuExample(Display display, MIDlet midlet) {
            // Force the smaller tile size to avoid using too much heap.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);

            alert = new Alert("");
            alert.setTimeout(1000);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Removes unnecessary map components.
            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            } else {
                note("Touch not enabled.", 5000);
            }

            // Add the context Menu component FIRST so that it is processed
            // LAST.
            contextMenus = new ContextMenuComponent(this, getCommandListener());
            map.addMapComponent(contextMenus);

            // Add the Focal component SECOND to feed the info bubble component
            // ABOVE
            focalComponent = new FocalObserverComponent(this);
            map.addMapComponent(focalComponent);

            // Finally add the Centering Component to feed the focal observer
            map.addMapComponent(new CenteringComponent(this));

            // Now we can set up the markers..
            addMarkerData(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
            addMarkerData(new GeoCoordinate(51.477811d, -0.001475d, 0), "London");
            addMarkerData(new GeoCoordinate(60.170833, 24.9375, 0),
                    "Helsinki    ");
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
         * The callback from the focal observer sets up the Context Menu. This
         * sets up a list of Menu items with an optional title.
         */
        public void onFocusChanged(Object focus) {

            currentFocus = (String) focus;

            if (currentFocus != null) {

                ChoiceGroup list1 = new ChoiceGroup(currentFocus, Choice.POPUP);

                for (int i = 0; i < currentFocus.length(); i++) {
                    list1.append("item" + (i + 1), null);
                }
                contextMenus.addData(list1, commands);
            } else {

                contextMenus.clear();
            }
        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         * @param d
         */
        public void commandRun(final Command c) {
            // Handle the Context menu command presses.
            alert.setString(currentFocus + " " + c.getLabel() + " was pressed");
            display.setCurrent(alert);
        }
    }

}
