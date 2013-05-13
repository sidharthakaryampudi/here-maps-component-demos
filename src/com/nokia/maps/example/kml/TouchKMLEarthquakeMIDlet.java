package com.nokia.maps.example.kml;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.kml.Document;
import com.nokia.maps.kml.Feature;
import com.nokia.maps.kml.KMLFactory;
import com.nokia.maps.kml.KMLFactoryListener;
import com.nokia.maps.kml.KMLManager;
import com.nokia.maps.kml.KMLParserListener;
import com.nokia.maps.kml.component.KMLEventListener;
import com.nokia.maps.kml.component.KMLMapComponent;
import com.nokia.maps.kml.component.KMLResultSet;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.ui.helpers.HypertextLinkRunner;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * KML loading Midlet.
 */
public class TouchKMLEarthquakeMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new TouchKMLEarthquakeDemo(display, this);

    }

    protected String getTitle() {
        return "Touch KML Earthquake";
    }

    protected String getDescription() {
        return "The KML Earthquake demo has been optimised for touch "
                + " by adding centering and tooltip components. "
                + "The use of Commands is no longer required.";
    }

    /**
     *
     * Main displayable for KML example
     *
     */
    private class TouchKMLEarthquakeDemo extends MapCanvasExample implements
            KMLEventListener, KMLParserListener, KMLFactoryListener {

        private Feature currentPlaceMark = null;
        private KMLListView listView = null;
        private final TooltipComponent tooltips;
        private final CenteringComponent centeringComponent;

        private final Command SHOW_BALLOON_VIEW = new Command("Details",
                Command.OK, 1);
        private final Command SHOW_LIST_VIEW = new Command("List", Command.ITEM,
                2);
        private final HypertextLinkRunner hypertextRunner;

        /**
         * Creates new canvas.
         * @param display
         *            display is needed for event loop
         * @param midlet
         *            MIDlet is needed to exit the application
         */
        public TouchKMLEarthquakeDemo(Display display, MIDlet midlet) {
            // The KML File will display a LOT of object son the map.
            // This will require a lot of heap memory, therefore it is better to
            // force the MapCanvas to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);
            hypertextRunner = HypertextLinkRunner.getInstance(midlet);

            Orientation.init(midlet);

            centeringComponent = new CenteringComponent(this,
                    getCommandListener(), SHOW_BALLOON_VIEW);
            map.addMapComponent(centeringComponent);

            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            }
            map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

            tooltips = new TooltipComponent(this);
            map.addMapComponent(tooltips);

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

            parseKMLFile();

        }

        /**
         * This method parses the KML.
         */
        private void parseKMLFile() {
            // Parse the KML Document
            KMLManager parser = KMLManager.getInstance();

            progressStart("Parsing KML", "KML not parsed.");
            // Could also load the KML file from a URL using:
            //
            // parser.parseKML
            // ("http://api.maps.nokia.com/en/playground/examples/maps/res/kml/usgs/earthquakes.kml",
            // this );

            parser.parse(getClass().getResourceAsStream("/kml/earthquake.kml"),
                    this);
        }

        /**
         * Callback function that is fired when an attempt to parse a KML file
         * has succeeded.
         *
         * @param source
         *            The parser that has read in the KML source file.
         * @param document
         *            a KML document that has been parsed.
         */
        public void onParseComplete(KMLManager source, Document document) {
            progressEnd();

            // Attempt to create Map Objects from the parsed KML
            progressStart("Creating Map", "Map creation failed");
            KMLFactory.getInstance(mapFactory).createKMLResultSet(document, this);
        }

        /**
         * Callback function that occurs after the successful creation of a
         * KMLResultSet.
         *
         * @param source
         *            The factory that created the result set.
         * @param resultSet
         *            the result set that has been created.
         */
        public void onCreateKMLResultSetComplete(KMLFactory source,
                KMLResultSet resultSet) {

            map.addMapObject(resultSet.getContainer());

            // Could also to use zoomTo here.
            // map.zoomTo(resultSet.getContainer().getBoundingBox(), false);
            map.setCenter(new GeoCoordinate(37, -122, 0));
            map.setZoomLevel(5, 0, 0);

            if (resultSet.getFeatures().length > 0) {
                // Ensure we are able to highlight Icons and interact with
                // objects
                KMLMapComponent component = resultSet.getKMLMapComponent();

                component.setEventListener(this);
                map.addMapComponent(component);
                // Ensure we are able to select/deselect items.
                listView = new KMLListView(resultSet);
                listView.setCommandListener(getCommandListener());
                addCommand(SHOW_LIST_VIEW);
            }
            progressEnd();

        }

        /**
         * Callback function which is fired when a KML Document cannot be
         * parsed.
         *
         * @param source
         *            the parser which had been invoked.
         * @param error
         *            the reason the KML parse request has failed.
         */
        public void onParseError(KMLManager source, Throwable error) {
            error("Error during KML parse: " + error.toString());
        }

        /**
         * Callback function which is fired if a KML Result set cannot be
         * created
         *
         * @param source
         *            source of the event
         * @param error
         *            the reason that the result set has failed to be created
         */
        public void onCreateKMLResultSetError(KMLFactory source, Throwable error) {
            error("Error during Map Creation: " + error.toString());
        }

        /**
         * Either adds or clears the details of the current place mark.
         *
         * @param placeMark
         *            the current feature under the cursor or NULL if there is
         *            none.
         */
        public void onFocusChanged(Feature placeMark) {

            currentPlaceMark = placeMark;

            if (KMLBalloonView.isViewable(currentPlaceMark)) {
                tooltips.add(placeMark.getName());
                addCommand(SHOW_BALLOON_VIEW);
            } else { // no tweet under cursor
                tooltips.clear();
                removeCommand(SHOW_BALLOON_VIEW);
            }
        }

        /**
         * Called description thread executing the command
         */
        public void commandRun(Command c) {
            if (c == SHOW_BALLOON_VIEW) {
                showPlaceMarkDetails();
            } else if (c == SHOW_LIST_VIEW) {
                display.setCurrent(listView);
            } else if (c == KMLListView.OK) {
                // Re-displays all visible map objects.
                currentPlaceMark = null;
                removeCommand(SHOW_BALLOON_VIEW);
                display.setCurrent(this);
                map.removeAllMapObjects();
                map.addMapObject(listView.updateContainer());
                repaint();
            } else if (c == KMLBalloonView.OK || c == KMLListView.CANCEL) {
                display.setCurrent(this);
            }
        }

        /**
         * Shows current Place Mark details dialog.
         */
        private void showPlaceMarkDetails() {
            KMLBalloonView balloonView = new KMLBalloonView(currentPlaceMark,
                    hypertextRunner);

            balloonView.setCommandListener(getCommandListener());
            display.setCurrent(balloonView);
        }

        /**
         * Overrides method description canvas to handle fire key
         */
        protected void keyReleased(int key) {
            if (getGameAction(key) == Canvas.FIRE && currentPlaceMark != null) {
                getCommandListener().commandAction(SHOW_BALLOON_VIEW, this);
            }
            super.keyReleased(key);
        }
    }

}
