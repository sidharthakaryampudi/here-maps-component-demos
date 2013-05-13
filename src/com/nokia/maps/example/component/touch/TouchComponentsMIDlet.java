package com.nokia.maps.example.component.touch;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.ContextMenuComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.example.component.button.Overview;
import com.nokia.maps.example.component.button.ScaleBar;
import com.nokia.maps.example.location.Positioning;
import com.nokia.maps.example.maptype.TypeSelector;
import com.nokia.maps.example.maptype.TypeSelectorUI;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets a demo of multiple touchable map Components as current
 * Displayable.
 */
public class TouchComponentsMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new TouchComponentsDemo(display, this);

    }

    protected String getTitle() {
        return "Custom Touch Components";
    }

    protected String getDescription() {
        return "A variety of custom touch components are added to the MapCanvas "
                + "These include, tooltips, a context menu and button controls to "
                + "change the map type, locate the device and show an overview of the map";
    }

    /**
     * Demonstrates adding a variety of Custom Components.
     */
    private class TouchComponentsDemo extends GestureMapCanvasExample {

        private final TooltipComponent tooltips;
        private final ContextMenuComponent contextMenus;
        // Only set up these buttons if touch is enabled.
        private TypeSelector typeSelector;
        private Positioning positioning;
        private Overview overview;
        private ScaleBar scalebar;
        private final Command DO1 = new Command("1", Command.ITEM, 1);
        private final Command DO2 = new Command("2", Command.ITEM, 2);
        private final Command DO3 = new Command("3", Command.ITEM, 3);
        private final Command DO4 = new Command("4", Command.ITEM, 4);
        private final Command DO5 = new Command("5", Command.ITEM, 5);
        private final Command DO6 = new Command("6", Command.ITEM, 6);
        private Command[] commands = { DO1, DO2, DO3, DO4, DO5, DO6};
        private final Command SCALE_BAR_COMMAND = new Command("Scale Bar",
                Command.ITEM, 1);
        private final Command POSITIONING_COMMAND = new Command("Positioning",
                Command.ITEM, 2);
        private final Command OVERVIEW_COMMAND = new Command("Overview",
                Command.ITEM, 3);
        private final Command TYPE_SELECTOR_COMMAND = new Command(
                "Type Selector", Command.ITEM, 4);
        private Thread overviewUpdater;

        private final GeoBoundingBox EUROPE = new GeoBoundingBox(
                new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));

        /**
         * Constructor for the Map Component Demo.
         *
         * @param display
         * @param midlet
         */
        public TouchComponentsDemo(Display display, MIDlet midlet) {
            super(display, midlet);

            // Allows landscape or Portrait Mode where applicable.
            Orientation.init(midlet);

            // Removes unnecessary map components.
            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
                map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
            } else {
                note("Touch not enabled.", 5000);
            }
            // Set up the Tooltip and Context Menu components FIRST.
            tooltips = new TooltipComponent(this);
            map.addMapComponent(tooltips);

            contextMenus = new ContextMenuComponent(this, getCommandListener());
            map.addMapComponent(contextMenus);

            // Set up the Map Marker centerer afterwards, so that the
            // centerer fires first and the tooltips and context menus are
            // handled afterwards.
            map.addMapComponent(new CenteringComponent(this));

            // Set up a variety of buttons on screen.
            setUpButtons();

            // Add four tooltips and a context menu.
            // The data can be add in directly here, or it could be provided
            // by a focal observer.
            addCityMarker(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
            addCityMarker(new GeoCoordinate(51.477811d, -0.001475d, 0),
                    "London, this is the city where the Olympic Games took place in the summer of 2012.");
            addCityMarker(new GeoCoordinate(60.170833, 24.9375, 0), "Helsinki");
            addCityMarker(new GeoCoordinate(59.949444, 10.756389, 0), "Oslo");

            addCityMarkerMenu(new GeoCoordinate(45.4375, 12.335833, 0), "Venice");

            // Set up the map, this will initially display a map of Europe.
            map.zoomTo(EUROPE, false);
            moveZoomButtonToBack();

            if (!hasPointerEvents()) {
                //
                // Since touch is unaavailable, access the buttons via
                // a command instead.
                //
                addCommand(SCALE_BAR_COMMAND);
                addCommand(POSITIONING_COMMAND);
                addCommand(OVERVIEW_COMMAND);
                addCommand(TYPE_SELECTOR_COMMAND);
            }

        }

        private void setUpButtons() {

            // Add a mapType Component, Positioning and Pic-in-Pic Overview.
            // These buttons require touch events to work.
            try {
                typeSelector = new TypeSelector(display, this);
                map.addMapComponent(typeSelector);

                positioning = new Positioning(this);

                positioning.setOffset(40, 10);
                map.addMapComponent(positioning);

                overview = new Overview();

                map.addMapComponent(overview);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            // Add a scalebar bar component. This should display the
            // appropriate default units to the user.
            scalebar = new ScaleBar(Graphics.BOTTOM | Graphics.LEFT);

            scalebar.setOffset(10, 30);
            map.addMapComponent(scalebar);

        }

        /**
         * Helper function to add tooltip texts.
         *
         * @param coord
         * @param text
         */
        private void addCityMarker(GeoCoordinate coord, String text) {
            MapStandardMarker marker = mapFactory.createStandardMarker(coord);

            tooltips.add(marker, text);

            map.addMapObject(marker);
        }

        /**
         * Helper function to add in a Context menu.
         *
         * @param coord
         * @param text
         */
        private void addCityMarkerMenu(GeoCoordinate coord, String text) {
            MapStandardMarker marker = mapFactory.createStandardMarker(coord);
            ChoiceGroup list1 = new ChoiceGroup(text, Choice.POPUP);

            list1.append("item1", null);
            list1.append("item2", null);
            list1.append("item3", null);
            list1.append("item4", null);
            list1.append("item5", null);
            list1.append("item6", null);

            contextMenus.addData(marker, list1, commands);

            map.addMapObject(marker);
        }

        /**
         * Standard Command button interaction.
         *
         * @param c
         * @param d
         */
        public void commandRun(final Command c) {
            if (c == DO1 || c == DO2 || c == DO3 || c == DO4 || c == DO5
                    || c == DO6) {
                // Put something up on screen since a context menu has been
                // pressed.
                Alert alert = new Alert("");

                alert.setTimeout(1000);
                alert.setString(c.getLabel() + " was pressed");
                display.setCurrent(alert);
            } else if (c == SCALE_BAR_COMMAND) {
                scalebar.toggleButton();
                onMapContentUpdated();
            } else if (c == POSITIONING_COMMAND) {
                positioning.toggleButton();
                onMapContentUpdated();
            } else if (c == OVERVIEW_COMMAND) {
                // The  overview button toggle is on a separate thread, since the overview
                // control will initiate an http request.
                overviewUpdater = new Thread(new Runnable() {
                    public void run() {
                        overview.toggleButton();
                        onMapContentUpdated();
                    }
                });
                overviewUpdater.start();
            } else if (c == TYPE_SELECTOR_COMMAND) {
                typeSelector.toggleButton();
                onMapContentUpdated();
            }
            // Handles The IconCommand.BACK action if pressed.
            TypeSelectorUI.handleCommandAction(c, display.getCurrent());
        }
    }
}
