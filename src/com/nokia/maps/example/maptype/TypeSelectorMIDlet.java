package com.nokia.maps.example.maptype;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets the Map Type Selector demo as current Displayable.
 */
public class TypeSelectorMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new TypeSelectorExample(display, this);
    }

    protected String getTitle() {
        return "Map Type Selector Component";
    }

    protected String getDescription() {
        return "Demonstrates adding a map type selector button to the screen. This allows "
                + "the user to switch between the five standard map types. The button is a "
                + "simple toggle, the UI is delegated to a separate class.";
    }

    /**
     * Demonstrates adding a map type selector button to the screen. This allows
     * the user to switch between the five standard map types. The button is a
     * simple toggle.
     */
    private class TypeSelectorExample extends MapCanvasExample {

        private final Command TYPE_SELECTOR_COMMAND = new Command("Map Type",
                Command.ITEM, 1);
        private final TypeSelector typeSelector;

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Map Type selector demo.
         *
         * @param display
         * @param midlet
         */
        public TypeSelectorExample(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Add the button - the try catch is in case the Images fail to
            // load.
            try {
                typeSelector = new TypeSelector(display, this);

                map.addMapComponent(typeSelector);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);

            if (!hasPointerEvents()) {
                //
                // Since touch is unavailable, access the type selector via
                // a command instead.
                //
                addCommand(TYPE_SELECTOR_COMMAND);
                note("Touch not enabled.", 5000);
            }

        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         */
        public void commandRun(final Command c) {

            if (c == TYPE_SELECTOR_COMMAND) {
                // For Non- touch phones only use a command button.
                typeSelector.toggleButton();
                onMapContentUpdated();
            }

            // Handles The IconCommand.BACK action if pressed.
            TypeSelectorUI.handleCommandAction(c, display.getCurrent());
        }
    }
}
