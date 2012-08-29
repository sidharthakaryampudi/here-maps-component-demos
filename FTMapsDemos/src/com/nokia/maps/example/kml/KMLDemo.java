package com.nokia.maps.example.kml;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.example.Base;
import com.nokia.maps.kml.Document;
import com.nokia.maps.kml.Feature;
import com.nokia.maps.kml.KMLFactory;
import com.nokia.maps.kml.KMLFactoryListener;
import com.nokia.maps.kml.KMLManager;
import com.nokia.maps.kml.KMLParserListener;
import com.nokia.maps.kml.component.KMLEventListener;
import com.nokia.maps.kml.component.KMLMapComponent;
import com.nokia.maps.kml.component.KMLResultSet;


/**
 *
 * Main displayable for KML example
 */
public class KMLDemo extends Base implements KMLEventListener, KMLParserListener, KMLFactoryListener {

    private final static Command SHOW_BALLOON_VIEW = new Command("Details", Command.OK, 1);
    private final static Command SHOW_LIST_VIEW = new Command("List", Command.ITEM,
            2);
    private Feature currentPlaceMark = null;
    private KMLListView listView = null;
    private final TooltipComponent tooltips;


    /**
     * Creates new canvas.
     */
    public KMLDemo(Display display, MIDlet midlet) {
        super(display, midlet);
        
        tooltips = new TooltipComponent();
		map.addMapComponent(tooltips);

        // Parse the KML Document
        KMLManager parser = KMLManager.getInstance();

        progressStart("Parsing KML", "KML not parsed.");
        // Could also load the KML file from a URL using:
        // parser.parseKML ("http://api.maps.nokia.com/2.1.0/playground/examples/res/kml/usgs/earthquakes.kml", this );

        parser.parse(getClass().getResourceAsStream("/earthquake.kml"), this);
        
       centeringComponent.setFocusCommand( SHOW_BALLOON_VIEW);

    }
    
    /**
     * Callback function that is fired when an attempt to parse a KML file has succeeded.
     * @param source The parser that has read in the KML source file.
     * @param document a KML document that has been parsed.
     */
    public void onParseComplete(KMLManager source, Document document) {
        progressEnd();

        // Attempt to create Map Objects from the parsed KML
        progressStart("Creating Map", "Map creation failed");
        KMLFactory.getInstance(mapFactory).createKMLResultSet(document, this);
    }

    /**
     * Callback function that occurs after the successful creation of a KMLResultSet.
     *
     * @param source The factory that created the result set.
     * @param resultSet  the result set that has been created.
     */
    public void onCreateKMLResultSetComplete(KMLFactory source, KMLResultSet resultSet) {

        map.addMapObject(resultSet.getContainer());

        // Could also to use zoomTo here.
        // map.zoomTo(resultSet.getContainer().getBoundingBox(), false);
        map.setCenter(new GeoCoordinate(37, -122, 0));
        map.setZoomLevel(5, 0, 0);

        if (resultSet.getFeatures().length > 0) {
            // Ensure we are able to highlight Icons and interact with objects
            KMLMapComponent component = resultSet.getKMLMapComponent();

            component.setEventListener(this);
            map.addMapComponent(component);
            // Ensure we are able to select/deselect items.
            listView = new KMLListView(resultSet);
            listView.setCommandListener(this);
            addCommand(SHOW_LIST_VIEW);
        }
        progressEnd();
    }

    /**
     * Callback function which is fired when a KML Document cannot be parsed.
     * @param source the parser which had been invoked.
     * @param error the reason the KML parse request has failed.
     */
    public void onParseError(KMLManager source, Throwable error) {
        error("Error during KML parse: " + error.toString());
    }

    /**
     * Callback function which is fired if a KML Result set cannot be created
     * @param source source of the event
     * @param error the reason that the result set has failed to be created
     */
    public void onCreateKMLResultSetError(KMLFactory source, Throwable error) {
        error("Error during Map Creation: " + error.toString());
    }

    /**
     * Either adds or clears the details of the current place mark.
     * @param placeMark the current feature under the
     * cursor or NULL if there is none.
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
    protected void commandRun(Command c) {
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
        KMLBalloonView balloonView = new KMLBalloonView(currentPlaceMark);
        balloonView.setCommandListener(this);
        display.setCurrent(balloonView);
    }

    /**
     * Overrides method description canvas to handle fire key
     */
    protected void keyReleased(int key) {
        if (getGameAction(key) == Canvas.FIRE && currentPlaceMark != null) {
            commandAction(SHOW_BALLOON_VIEW, this);
        }
        super.keyReleased(key);
    }
}
