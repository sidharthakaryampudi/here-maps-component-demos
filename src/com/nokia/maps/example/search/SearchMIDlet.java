package com.nokia.maps.example.search;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.places.Address;
import com.nokia.places.Category;
import com.nokia.places.Location;
import com.nokia.places.PlaceFactory;
import com.nokia.places.PlaceLink;
import com.nokia.places.ResultPage;
import com.nokia.places.geo.request.GeocodeRequest;
import com.nokia.places.geo.request.GeocodeRequestListener;
import com.nokia.places.geo.request.ReverseGeocodeRequest;
import com.nokia.places.geo.request.ReverseGeocodeRequestListener;
import com.nokia.places.request.Modifier;
import com.nokia.places.request.ResultPageRequest;
import com.nokia.places.request.ResultPageRequestListener;
import javax.microedition.lcdui.Graphics;


/**
 * This MIDlet just sets demo as current Displayable.
 */
public class SearchMIDlet extends BaseMIDlet {

    private final TextBox searchBox = new TextBox("Enter address",
            "Berlin, Germany", 100, TextField.ANY);

    protected MapCanvas getDemo(Display display) {
        return new SearchDemo(display, this, searchBox);
    }

    protected String getTitle() {
        return "Search Demo";
    }

    protected String getDescription() {
        return "This example shows how  make  geocoding and reverse geocoding requests. "
                + "Geocoding is finding the best fit location for a given address, and "
                + "Reverse Geocoding is obtaining an address for a given location.";
    }

    protected void startDemo(Display display, MapCanvas demo) {
        // first show configuration displayable
        display.setCurrent(searchBox);
    }

    /**
     * This demonstrates how to make geocoding and reverse geocoding requests.
     */
    private class SearchDemo extends MapCanvasExample implements GeocodeRequestListener,
            ReverseGeocodeRequestListener, ResultPageRequestListener {

        private final Command CHOOSE_AN_ADDRESS = new Command("Geocode",
                Command.ITEM, 1);
        private final Command REVERSE_GEOCODE = new Command("Reverse\nGeocode",
                Command.ITEM, 2);
        private final Command VICINITY = new Command(
                "Formatted\nAddress within\n200 metres", Command.ITEM, 3);
        private final Command GEOCODE = new Command("Ok", Command.OK, 1);
        private PlaceFactory searchFactory = PlaceFactory.getInstance();

        /**
         * These components are used to display the addresses found.
         */
        private final TooltipComponent tooltip;
        private final CenteringComponent centerer;
        private final ButtonCommand geocodeButton;
        private final ButtonCommand revGeocodeButton;
        private final ButtonCommand placesRGeoButton;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        /**
         * @param display
         *            display is needed for event loop
         * @param midlet
         *            MIDlet is needed to exit the application
         * @param searchBox
         *   Details of the address to  geocode.
         */
        public SearchDemo(Display display, MIDlet midlet, TextBox searchBox) {
            super(display, midlet);
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 15));

            geocodeButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT, this,
                    getCommandListener(), CHOOSE_AN_ADDRESS);
            revGeocodeButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT,
                    this, getCommandListener(), REVERSE_GEOCODE);
            revGeocodeButton.setOffset(10, 40);
            placesRGeoButton = new ButtonCommand(Graphics.TOP | Graphics.RIGHT,
                    this, getCommandListener(), VICINITY);

            setCommandsVisible(false);
            searchBox.setTitle("Find a location from an Address");
            searchBox.addCommand(GEOCODE);
            searchBox.setCommandListener(getCommandListener());

            centerer = new CenteringComponent(this);
            map.addMapComponent(centerer);

            tooltip = new TooltipComponent(this);
            map.addMapComponent(tooltip);

        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(Command c) {
            if (c == CHOOSE_AN_ADDRESS) {
                // This brings up the dialog box

                display.setCurrent(searchBox);
            } else if (c == GEOCODE) {
                map.removeAllMapObjects();
                tooltip.clear();
                display.setCurrent(this);
                setCommandsVisible(false);
                // This does the geocoding request
                makeGeocodeRequest(searchBox.getString());
            } else if (c == REVERSE_GEOCODE) {

                display.setCurrent(this);
                setCommandsVisible(false);

                map.removeAllMapObjects();
                tooltip.clear();

                Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);

                // This does the reverse geocoding request
                makeReverseGeocodeRequest(map.pixelToGeo(center));
            } else if (c == VICINITY) {

                display.setCurrent(this);
                setCommandsVisible(false);

                map.removeAllMapObjects();
                tooltip.clear();

                Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);

                // This uses the place search here function to retrieve
                // places in the vicinity.
                makeVicinityRequest(map.pixelToGeo(center));
            }
        }

        // ///////////////////////////////////////////////////////////////////////
        //
        // The Following functions are used for GEOCODING.
        // i.e. to get a location for an address
        //
        // ///////////////////////////////////////////////////////////////////////
        /**
         * Make a geocode request for the address or partial address given
         *
         * @param addressText
         *            - the address text for the search.
         */
        private void makeGeocodeRequest(String addressText) {

            GeocodeRequest geocodeRequest = searchFactory.createGeocodeRequest();

            // An asynchronous request is made success and failure are
            // handled in the two callback functions described below.
            geocodeRequest.geocode(addressText, null, this);
            progressStart("Geocoding address..", "No locations were found.");
        }

        /**
         * Called when the geocoding search request has failed.
         *
         * @param request
         *            the request that effected the search
         * @param error
         *            the detail for the source of the error
         */
        public void onRequestError(GeocodeRequest request, Throwable error) {
            error(error.toString());
            setCommandsVisible(true);
        }

        /**
         * Called when the geocoding search request has successfully completed
         * and a valid result obtained.
         *
         * @param request
         *            the request that effected the search
         * @param locations
         *            the locations that resulted from the request
         */
        public void onRequestComplete(GeocodeRequest request,
                Location[] locations) {
            centerMapToFirstSearchResult(locations);
            for (int i = 0; i < locations.length; i++) {
                MapStandardMarker marker = mapFactory.createStandardMarker(
                        locations[i].getDisplayPosition(), 10,
                        String.valueOf(i + 1), MapStandardMarker.BALLOON);

                map.addMapObject(marker);
                // The label holds the title of the location.
                tooltip.add(marker, locations[i].getLabel());
            }
            setCommandsVisible(true);

        }

        // ///////////////////////////////////////////////////////////////////////
        //
        // The Following functions are used for REVERSE GEOCODING.
        // i.e. to get all the elements which make up an address for a location.
        // The address returned is unformatted.
        //
        // ///////////////////////////////////////////////////////////////////////
        /**
         * Make a reverse geocode request for the center of the map.
         *
         * @param gc
         *            The coordinate to reverse geocode.
         */
        private void makeReverseGeocodeRequest(GeoCoordinate gc) {

            Modifier modifier = searchFactory.createRequestModifier();
            modifier.setPageSize(1); // Only want to get the best match.
            ReverseGeocodeRequest reverseGeocodeRequest =
                    searchFactory.createReverseGeocodeRequest(modifier);

            // An asynchronous request is made success and failure are
            // handled in the two callback functions described below.
            reverseGeocodeRequest.reverseGeocode(gc, this);
            progressStart("Reverse Geocoding location..",
                    "No address found for this location.");
        }

        /**
         * Called when the reverse geocoding search request has failed.
         *
         * @param request
         *            the request that effected the search
         * @param error
         *            the detail for the source of the error
         */
        public void onRequestError(ReverseGeocodeRequest request,
                Throwable error) {
            error(error.toString());
            setCommandsVisible(true);
        }

        /**
         * Called when the reverse geocoding search request has successfully
         * completed and a valid result obtained.
         *
         * @param request
         *            the request that effected the search
         * @param locations
         *            the locations that resulted from the request
         */
        public void onRequestComplete(ReverseGeocodeRequest request,
                Location[] locations) {
            centerMapToFirstSearchResult(locations);
            for (int i = 0; i < locations.length; i++) {
                MapStandardMarker marker = mapFactory.createStandardMarker(
                        locations[i].getDisplayPosition(), 10,
                        String.valueOf(i + 1), MapStandardMarker.BALLOON);

                map.addMapObject(marker);

                if (locations[i].getAddress() != null) {
                    tooltip.add(marker,
                            displayAddressElements(locations[i].getAddress()));
                }
            }
            setCommandsVisible(true);
            onMapContentUpdated();

        }

        /**
         * Helper function to display the result of a reverse geocoding request
         * in a tooltip.
         *
         * @param address
         *            The Address object associated with the location
         * @return the full address as a <code>String</code>
         */
        private String displayAddressElements(Address address) {
            StringBuffer buf = new StringBuffer();

            appendIfNotNull(buf, address.getHouseNumber());
            appendIfNotNull(buf, address.getStreet());
            appendIfNotNull(buf, address.getDistrict());
            appendIfNotNull(buf, address.getCounty());
            appendIfNotNull(buf, address.getState());
            appendIfNotNull(buf, address.getCountryName());

            return buf.toString();
        }

        /**
         * Adds a field to the string buffer if it is not null.
         *
         * @param buf
         * @param field
         */
        private void appendIfNotNull(StringBuffer buf, String field) {
            if (field != null) {
                buf.append(field);
                buf.append(" ");
            }
        }

        // ///////////////////////////////////////////////////////////////////////
        //
        // The Following functions are used for a vicinity request.
        // i.e. to get places within 200m of a location, the nearest location
        // will be used to obtain an address. This will have a correctly
        // formatted address for the location. For example, addresses in Germany
        // will place the house number AFTER the street name.
        //
        // ///////////////////////////////////////////////////////////////////////
        /**
         * Make a reverse geocode request for the center of the map.
         *
         * @param gc
         *            The coordinate to reverse geocode.
         */
        private void makeVicinityRequest(GeoCoordinate gc) {

            Modifier modifier = searchFactory.createRequestModifier();

            modifier.setPageSize(1);
            ResultPageRequest vicinityRequest = searchFactory.createResultPageRequest(
                    modifier);

            // An asynchronous request is made success and failure are
            // handled in the two callback functions described below.
            vicinityRequest.here(Category.UNFILTERED, gc, this);
            progressStart("Finding nearest place..",
                    "No address found for this location.");
        }

        /**
         * Called when the here search request has failed.
         *
         * @param request
         *            the request that effected the search
         * @param error
         *            the detail for the source of the error
         */
        public void onRequestError(ResultPageRequest request,
                Throwable error) {
            error(error.toString());
            setCommandsVisible(true);
        }

        /**
         * Called when the here search request has successfully
         * completed and a valid result obtained.
         *
         * @param request
         *            the request that effected the search
         * @param locations
         *            the locations that resulted from the request
         */
        public void onRequestComplete(ResultPageRequest request,
                ResultPage result) {

            progressEnd();

            if (result.getNumberOfItems() > 0) {
                map.setCenter(result.getItem(0).getPosition());
            } else {
                error("Location not found.");
            }
            PlaceLink[] locations = result.getItems();

            for (int i = 0; i < locations.length; i++) {
                MapStandardMarker marker = mapFactory.createStandardMarker(
                        locations[i].getPosition(), 10, String.valueOf(i + 1),
                        MapStandardMarker.BALLOON);

                map.addMapObject(marker);

                if (locations[i].getVicinity() != null) {
                    tooltip.add(marker, locations[i].getVicinity().getText());
                }
            }
            setCommandsVisible(true);
            onMapContentUpdated();

        }

        /**
         * Search address and center map display to it
         *
         * @param locations
         *            the addresses found.
         */
        private void centerMapToFirstSearchResult(Location[] locations) {
            progressEnd();

            if (null != locations && locations.length > 0) {
                map.setCenter(locations[0].getDisplayPosition());
            } else {
                error("Location not found.");
            }
        }

        /**
         * Adds or removes the command items as required.
         *
         * @param isVisible
         *            add/or remove.
         */
        private void setCommandsVisible(boolean isVisible) {

            if (isVisible) {
                map.addMapComponent(geocodeButton);
                map.addMapComponent(revGeocodeButton);
                map.addMapComponent(placesRGeoButton);
            } else {
                map.removeMapComponent(geocodeButton);
                map.removeMapComponent(revGeocodeButton);
                map.removeMapComponent(placesRGeoButton);
            }

        }
    }
}
