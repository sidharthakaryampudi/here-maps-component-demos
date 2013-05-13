package com.nokia.maps.example.overlay;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapProvider;
import javax.microedition.lcdui.Graphics;


/**
 * Example MIDP application to show a historic map overlay to the user.
 */
public class OverlayMidlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new OverlayExample(display, this);
    }

    protected String getTitle() {
        return "Overlay Example";
    }

    protected String getDescription() {
        return "This example overlays a historical map of Berlin from 1789 on top of the base map.\n"
                + "As a source we used an original public domain image and split it into individual map tiles.\n\n"
                + "see: http://commons.wikimedia.org/wiki/File:Map_de_berlin_1789_%28georeferenced%29.jpg";
    }

    /**
     * This Map Canvas is a demonstration in the use of an Overlay. The overlay
     * is taken from a tile server run on api.maps.nokia.com
     */
    private class OverlayExample extends MapCanvasExample {

        private final MapProvider overlay;
        private final Command ADD = new Command("Add Overlay", Command.ITEM, 1);
        private final Command REMOVE = new Command("Remove Overlay",
                Command.ITEM, 1);
        private final ButtonCommand addOverlayButton;
        private final ButtonCommand removeOverlayButton;

        /**
         * Start by displaying a normal.day map. Initialize the overlay for
         * later use.
         *
         * @param display
         * @param midlet
         */
        public OverlayExample(Display display, MIDlet midlet) {
            // Overlays can require a lot of heap memory.
            // Force the example to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);

            // Start with the map centred over Berlin, Germany.
            map.setState(
                    new MapDisplayState(new GeoCoordinate(52.51, 13.4, 0), 13));

            // Create and add the overlay.
            overlay = new OldBerlinProvider(map.getResolution());
            map.addMapOverlay(overlay);

            addOverlayButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), ADD);
            removeOverlayButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), REMOVE);
            map.addMapComponent(removeOverlayButton);

        }

        /**
         * Command button to add/remove the overlay.
         *
         * @param c
         *
         */
        public void commandRun(Command c) {
            if (c == ADD) {
                map.addMapOverlay(overlay);
                // Switch buttons.
                map.removeMapComponent(addOverlayButton);
                map.addMapComponent(removeOverlayButton);

            } else if (c == REMOVE) {
                map.removeMapOverlay(overlay);
                // Switch buttons.
                map.addMapComponent(addOverlayButton);
                map.removeMapComponent(removeOverlayButton);
            }
        }
    }
}
