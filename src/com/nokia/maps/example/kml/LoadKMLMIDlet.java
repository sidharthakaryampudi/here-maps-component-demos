/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.kml;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

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
import com.nokia.maps.map.MapContainer;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapSchemeType;


/**
 * KML loading Midlet.
 */
public class LoadKMLMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new LoadKMLExample(display, this);
    }

    protected String getTitle() {
        return "Loading a KML file";
    }

    protected String getDescription() {
        return "The KML Manager can parse KML files and display them onto the map. ";
    }

    /**
     *
     * Main displayable for KML example
     */
    public class LoadKMLExample extends MapCanvasExample implements KMLEventListener,
            KMLParserListener, KMLFactoryListener {

        private final Command SHOW_NAME = new Command("Details", Command.OK, 1);
        private Feature currentPlaceMark = null;
        private MapContainer airportContainer;
        private MapContainer terminalsContainer;
        private MapContainer runwayContainer;
        private final Command ZOOM_TO_TERMINALS = new Command(
                "Zoom to Terminals", Command.ITEM, 1);
        private final Command ZOOM_TO_RUNWAY = new Command("Zoom to Runway",
                Command.ITEM, 2);
        private final Command ZOOM_TO_ALL = new Command("Zoom to All",
                Command.ITEM, 3);

        /**
         * Creates new canvas.
         * @param display
         *            display is needed for event loop
         * @param midlet
         *            MIDlet is needed to exit the application
         */
        public LoadKMLExample(Display display, MIDlet midlet) {
            super(display, midlet);

            map.setBaseMapType(MapSchemeType.SATELLITE);

            // Parse the KML Dcounment
            KMLManager parser = KMLManager.getInstance();

            progressStart("Parsing KML", "KML not parsed.");
            // Could also load the KML file from a URL using:
            parser.parseKML(
                    "http://api.maps.nokia.com/en/playground/examples/maps/res/kml/berlin_airport/schoenefeld.kml",
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

            addCommand(ZOOM_TO_TERMINALS);
            addCommand(ZOOM_TO_RUNWAY);
            addCommand(ZOOM_TO_ALL);

            airportContainer = resultSet.getContainer();
            MapObject[] objects = airportContainer.getAllMapObjects();

            runwayContainer = (MapContainer) objects[1];
            terminalsContainer = (MapContainer) objects[2];

            map.zoomTo(resultSet.getContainer().getBoundingBox(), false);

            if (resultSet.getFeatures().length > 0) {
                // Ensure we are able to highlight Icons and interact with
                // objects
                KMLMapComponent component = resultSet.getKMLMapComponent();

                component.setEventListener(this);
                map.addMapComponent(component);

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
            if (currentPlaceMark != null && currentPlaceMark.getName() != null) {
                addCommand(SHOW_NAME);
            } else { // no element under cursor
                removeCommand(SHOW_NAME);
            }
        }

        /**
         * Called description thread executing the command
         */
        public void commandRun(Command c) {

            if (c == ZOOM_TO_TERMINALS) {
                map.zoomTo(terminalsContainer.getBoundingBox(), false);
            }
            if (c == ZOOM_TO_RUNWAY) {
                map.zoomTo(runwayContainer.getBoundingBox(), false);
            }
            if (c == ZOOM_TO_ALL) {
                map.zoomTo(airportContainer.getBoundingBox(), false);
            }
            if (c == SHOW_NAME) {
                Alert alertView = new Alert("", currentPlaceMark.getName(), null,
                        AlertType.INFO);

                display.setCurrent(alertView);

            }

        }
    }

}
