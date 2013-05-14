/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.place;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.InfoBubbleComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.example.component.button.ButtonCommand;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapContainer;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.maps.sharing.SharingManager;
import com.nokia.maps.ui.helpers.HypertextLinkRunner;
import com.nokia.maps.ui.helpers.Orientation;
import com.nokia.places.Category;
import com.nokia.places.PlaceFactory;
import com.nokia.places.PlaceLink;
import com.nokia.places.ResultPage;
import com.nokia.places.request.ResultPageRequest;
import com.nokia.places.request.ResultPageRequestListener;
import javax.microedition.lcdui.Graphics;


/**
 * This MIDlet just sets demo as current Displayable.
 */
public class TouchPlaceMIDlet extends BaseMIDlet {

    private final Command BACK = new Command("Back", Command.BACK, 1);

    protected MapCanvas getDemo(Display display) {
        return new TouchPlaceDemo(display, this);

    }

    protected String getTitle() {
        return "Touch Place Demo";
    }

    protected String getDescription() {
        return "The Place Demo demo has been optimised for touch "
                + " by adding focal observer, centering and infobubble components. ";
    }

    /**
     * This demonstrates Place search usage.
     */
    private class TouchPlaceDemo extends GestureMapCanvasExample implements
            ResultPageRequestListener, FocalEventListener {

        private final Command CATEGORY_SEARCH = new Command("Category Search",
                Command.ITEM, 4);
        private final Command SHOW_PLACE_DETAILS = new Command("Details",
                Command.ITEM, 3);
        private final Command SEARCH = new Command("Search for a", Command.ITEM,
                3);
        private final Command OK = new Command("Ok", Command.OK, 1);
        private final TextBox searchBox = new TextBox("Enter search term",
                "Cafe", 100, TextField.ANY);
        private PlaceLink currentPlace;
        private final PlaceDetailsForm placeDetailsForm;
        private final CategoryForm categoryForm;
        private ResultPage page;
        private final FocalObserverComponent placeDataComponent;
        private final CenteringComponent centeringComponent;
        private final InfoBubbleComponent infoBubble;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        private final ButtonCommand searchButton;
        private final ButtonCommand categoryButton;
        private final MapContainer resultsContainer;

        /**
         * @param display
         *            display is needed for event loop
         * @param midlet
         *            MIDlet is needed to exit the application
         */
        public TouchPlaceDemo(Display display, MIDlet midlet) {
            // Force the example to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);
            // This will hold the result set
            resultsContainer = getMapFactory().createMapContainer();
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));
            // The categories should use the same location as a hint.
            categoryForm = new CategoryForm(BERLIN);

            // initialise a couple of buttons for the canvas.
            searchButton = new ButtonCommand(Graphics.TOP | Graphics.LEFT, this,
                    getCommandListener(), SEARCH);
            categoryButton = new ButtonCommand(Graphics.TOP | Graphics.RIGHT,
                    this, getCommandListener(), CATEGORY_SEARCH);
            map.addMapComponent(searchButton);
            map.addMapComponent(categoryButton);

            Orientation.init(midlet);

            centeringComponent = new CenteringComponent(this,
                    getCommandListener(), null);
            map.addMapComponent(centeringComponent);

            infoBubble = new InfoBubbleComponent(this, getCommandListener());
            map.addMapComponent(infoBubble);

            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            } else {
                note("Touch not enabled.", 5000);
            }
            map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

            placeDataComponent = new FocalObserverComponent(this);
            map.addMapComponent(placeDataComponent);

            placeDetailsForm = new PlaceDetailsForm(
                    HypertextLinkRunner.getInstance(midlet));

        }

        /**
         * Generates an appropriate URL to share for the command
         *
         * @param c
         *            the button that was pressed.
         */
        private void handlePoiToUrl(Command c) {
            String generatedUrl = "";

            if (c == PlaceDetailsForm.SHARE_THIS_PLACE) {
                generatedUrl = SharingManager.getInstance().getPlaceUrl(
                        currentPlace);

            }
            Alert a = new Alert("Generated URL", generatedUrl, null,
                    AlertType.INFO);

            a.setTimeout(Alert.FOREVER);
            display.setCurrent(a, this);
        }

        /**
         * Method to initiate the search.
         */
        private void handleSearchRequest() {
            display.setCurrent(this);
            map.removeMapComponent(searchButton);
            map.removeMapComponent(categoryButton);
            removeCommand(OK);
            progressStart("Searching place..", "Place was not found.");

            GeoCoordinate topLeft = map.pixelToGeo(new Point(0, 0));
            GeoCoordinate bottomRight = map.pixelToGeo(
                    new Point(map.getWidth(), map.getHeight()));
            GeoBoundingBox bb = new GeoBoundingBox(topLeft, bottomRight);

            PlaceFactory sf = PlaceFactory.getInstance();
            ResultPageRequest placeSearchRequest = sf.createResultPageRequest();

            placeSearchRequest.search(searchBox.getString(), bb, this);
        }

        /**
         * Method to initiate a category based search.
         *
         * @param category
         *            The category to use to search for places.
         */
        private void handleCategorySearchRequest(Category category) {
            display.setCurrent(this);
            map.removeMapComponent(searchButton);
            map.removeMapComponent(categoryButton);
            removeCommand(OK);
            progressStart("Searching for " + category.getTitle(),
                    "No " + category.getTitle() + " places found.");

            PlaceFactory sf = PlaceFactory.getInstance();
            ResultPageRequest placeSearchRequest = sf.createResultPageRequest();

            placeSearchRequest.around(new Category[] { category },
                    map.getCenter(), this);
        }

        /**
         * Method to add markers indicating the places returned in the search.
         * @param placeLinks the list of places to add.
         */
        private void markResults(PlaceLink[] placeLinks) {
            map.removeMapObject(resultsContainer);
            resultsContainer.removeAllMapObjects();
            for (int i = 0; i < placeLinks.length; i++) {
                MapStandardMarker marker = mapFactory.createStandardMarker(
                        placeLinks[i].getPosition(), 8, String.valueOf(i + 1),
                        MapStandardMarker.BALLOON);

                resultsContainer.addMapObject(marker);
                placeDataComponent.addData(marker, placeLinks[i]);
            }
            map.addMapObject(resultsContainer);
            map.zoomTo(resultsContainer.getBoundingBox(), true);
        }

        /**
         * Called from thread executing the command
         *
         * @param c
         *            Command being run
         */
        public void commandRun(Command c) {
            if (c == SHOW_PLACE_DETAILS) {
                placeDetailsForm.makePlaceRequest(currentPlace);
                placeDetailsForm.setCommandListener(getCommandListener());
                placeDetailsForm.addCommand(BACK);
                display.setCurrent(placeDetailsForm);
            } else if (c == SEARCH) {
                map.removeAllMapObjects();
                placeDataComponent.clear();
                centeringComponent.setFocusCommand(null);

                searchBox.addCommand(OK);
                searchBox.setCommandListener(getCommandListener());
                display.setCurrent(searchBox);
            } else if (c == CATEGORY_SEARCH) {
                categoryForm.setSearchLocation(map.getCenter());
                categoryForm.setCommandListener(getCommandListener());
                categoryForm.addCommand(BACK);
                display.setCurrent(categoryForm);

            } else if (c == CategoryForm.CATEGORY_SEARCH) {
                placeDataComponent.clear();
                map.removeAllMapObjects();
                handleCategorySearchRequest(categoryForm.getCategory());
            } else if (c == BACK) {
                display.setCurrent(this);
                onMapContentUpdated();
            } else if (c == PlaceDetailsForm.GET_EDITORIALS) {
                placeDetailsForm.makeEditorialRequest(false);
            } else if (c == PlaceDetailsForm.GET_REVIEWS) {
                placeDetailsForm.makeReviewRequest(false);
            } else if (c == PlaceDetailsForm.GET_IMAGES) {
                placeDetailsForm.makeImageRequest(false);
            } else if (c == PlaceDetailsForm.MORE_EDITORIALS) {
                placeDetailsForm.makeEditorialRequest(true);
            } else if (c == PlaceDetailsForm.MORE_REVIEWS) {
                placeDetailsForm.makeReviewRequest(true);
            } else if (c == PlaceDetailsForm.MORE_IMAGES) {
                placeDetailsForm.makeImageRequest(true);
            } else if (c == OK) {
                handleSearchRequest();
            } else {
                handlePoiToUrl(c);
            }
        }

        /**
         * Search address and center map display to it
         *
         * @param address
         *            Address to search.
         */
        private void centerMapToFirstSearchResult() {
            if (page.getItems().length > 0) {
                map.setCenter(page.getItems()[0].getPosition());
            } else {
                error("No places found.");
            }
        }

        /**
         * Callback function from the place request.
         *
         * @param request
         *            the initiating request.
         * @param result
         *            The places found (if any )
         */
        public void onRequestComplete(ResultPageRequest request,
                ResultPage result) {
            page = result;
            map.addMapComponent(searchButton);
            map.addMapComponent(categoryButton);

            if (page.getItems().length > 0) {
                markResults(page.getItems());
                centerMapToFirstSearchResult();
                progressEnd();
            } else {
                error("Search returned no results.");
            }
        }

        /**
         * Callback function should the Place Request fail for some reason.
         *
         * @param request
         *            the initiating request.
         * @param error
         *            the reason the request failed.
         */
        public void onRequestError(ResultPageRequest request, Throwable error) {
            page = null;
            map.addMapComponent(searchButton);
            map.addMapComponent(categoryButton);
            error(error.toString());
        }

        /**
         * Callback when a Map object is at the centre of the screen
         *
         * @param focus
         *            - the data associated with the focal object.
         */
        public void onFocusChanged(Object focus) {

            currentPlace = (PlaceLink) focus;

            if (currentPlace != null) {
                StringBuffer summary = new StringBuffer(currentPlace.getTitle());

                summary.append("\n");
                summary.append(currentPlace.getCategory().getTitle());
                summary.append("\n");
                summary.append(currentPlace.getVicinity().getText());

                infoBubble.addData(summary.toString(), SHOW_PLACE_DETAILS);

            }
        }
    }
}
