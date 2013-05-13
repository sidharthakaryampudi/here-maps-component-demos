package com.nokia.maps.example.component.button;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Scale Bar demo as current Displayable.
 */
public class ScaleBarMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new ScaleBarExample(display, this);

    }

    protected String getTitle() {
        return "Scale Bar Component";
    }

    protected String getDescription() {
        return "Demonstrates adding a scale bar to the map.  The length of the scale bar "
                + "will alter on zoom and pan according to the Normalized Mercator projection, and the legend "
                + "will switch between Metric and Imperial Measurements.";
    }

    /**
     * Demonstrates adding a scale bar to the map. The length of the scale bar
     * will alter according to the Normalized Mercator projection. the legend
     * will switch between Metric and Imperial Measurements.
     */
    private class ScaleBarExample extends MapCanvasExample {

        private final Command SCALE_BAR_COMMAND = new Command("Scale Bar",
                Command.ITEM, 1);
        private final ScaleBar scale;
        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Scale Bar Demo Constructor
         * 
         * @param display
         * @param midlet
         */
        public ScaleBarExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Adds a Scale bar to the bottom left-hand corner.
            // The button is shifted slightly not to cover the
            // Nokia logo.
            scale = new ScaleBar(Graphics.BOTTOM | Graphics.LEFT);

            scale.setOffset(10, 30);
            map.addMapComponent(scale);

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

            if (!hasPointerEvents()) {
                //
                // Since touch is unavailable, access the overview via
                // a command instead.
                //
                addCommand(SCALE_BAR_COMMAND);
            }

        }

        /**
         * Standard Command button interaction.
         * 
         * @param c
         * 
         */
        public void commandRun(final Command c) {
            if (c == SCALE_BAR_COMMAND) {
                // For Non- touch phones only use a command button.
                scale.toggleButton();
                onMapContentUpdated();
            }
        }
    }
}
