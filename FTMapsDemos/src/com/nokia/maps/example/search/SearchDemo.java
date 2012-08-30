package com.nokia.maps.example.search;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.common.Location;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.example.Base;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.search.GeocodeRequest;
import com.nokia.maps.search.GeocodeRequestListener;
import com.nokia.maps.search.SearchFactory;


/**
 * This demonstrates SearchManager usage.
 */
public class SearchDemo extends Base implements GeocodeRequestListener,
        FocalEventListener {

    private final Command SEARCH = new Command("Search address", Command.ITEM, 3);
    private final Command OK = new Command("Ok", Command.OK, 1);
    private final TextBox searchBox = new TextBox("Enter address",
            "London, Britain", 100, TextField.ANY);
    private SearchFactory searchFactory = SearchFactory.getInstance();
    private Location[] locations; // search results

    private final FocalObserverComponent locationDataComponent;
    private final TooltipComponent tooltips;

    /**
     * @param display
     *            display is needed for event loop
     * @param midlet
     *            MIDlet is needed to exit the application
     */
    public SearchDemo(Display display, MIDlet midlet) {
        super(display, midlet);

        map.setState(new MapDisplayState(new GeoCoordinate(51.477, 0.0, 0), 15));

        addCommand(SEARCH);
		
        tooltips = new TooltipComponent();
        map.addMapComponent(tooltips);

        locationDataComponent = new FocalObserverComponent(this);
        map.addMapComponent(locationDataComponent);

    }

    /**
     * Search address and center map display to it
     * 
     * @param address
     *            Address to search.
     */
    private void centerMapToFirstSearchResult() {
        progressEnd();

        if (null != locations && locations.length > 0) {
            map.setCenter(locations[0].getDisplayPosition());
        } else {
            error("Location not found.");
        }
    }

    private void markResults() {
        for (int i = 0; i < locations.length; i++) {
            MapStandardMarker marker = mapFactory.createStandardMarker(
                    locations[i].getDisplayPosition(), 10, null,
                    MapStandardMarker.BALLOON);

            map.addMapObject(marker);
            locationDataComponent.addData(marker, locations[i]);
        }
    }

    public void onFocusChanged(Object focus) {

        Location location = (Location) focus;

        if (location != null) {
            tooltips.add(location.getLabel());
        } else {
            tooltips.clear();
        }
    }

    /**
     * Called from thread executing the command
     */
    protected void commandRun(Command c) {
        if (c == SEARCH) {
            map.removeAllMapObjects();
            locationDataComponent.clear();
            searchBox.addCommand(OK);
            searchBox.setCommandListener(this);
            display.setCurrent(searchBox);
        } else if (c == OK) {
            display.setCurrent(this);
            removeCommand(SEARCH);
            GeocodeRequest geocodeRequest = searchFactory.createGeocodeRequest();

            geocodeRequest.geocode(searchBox.getString(), null, this);
            progressStart("Searching address..", "Address was not found.");
        }
    }

    public void onRequestComplete(GeocodeRequest request, Location[] result) {
        locations = result;
        centerMapToFirstSearchResult();
        markResults();
        addCommand(SEARCH);

    }

    public void onRequestError(GeocodeRequest request, Throwable error) {
        locations = null;
        error(error.toString());
        addCommand(SEARCH);
    }
}
