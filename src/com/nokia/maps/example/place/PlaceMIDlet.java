package com.nokia.maps.example.place;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapContainer;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapSchemeType;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.sharing.SharingManager;
import com.nokia.maps.ui.helpers.HypertextLinkRunner;
import com.nokia.places.Category;
import com.nokia.places.PlaceFactory;
import com.nokia.places.PlaceLink;
import com.nokia.places.ResultPage;
import com.nokia.places.request.ResultPageRequest;
import com.nokia.places.request.ResultPageRequestListener;


/**
 * This MIDlet just sets demo as current Displayable.
 */
public class PlaceMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new PlaceDemo(display, this);

    }

    protected String getTitle() {
        return "Place Search Example";
    }

    protected String getDescription() {
        return "This example shows how to use the various Place search services in an app\n"
                + "Searches can be made by category or free text search, and"
                + " full details of an individual place can be displayed on screen.";
    }

    /**
     * This demonstrates Place search usage.
     */
    private class PlaceDemo extends MapCanvasExample implements ResultPageRequestListener,
            FocalEventListener {

        private final Command BACK = new Command("Back", Command.BACK, 1);
        private final Command SHOW_PLACE_DETAILS = new Command("Details",
                Command.ITEM, 3);
        private final Command SHOW_PLACE_SUMMARY = new Command("Summary",
                Command.ITEM, 2);
        private final Command SHOW_SEARCH_SUGGESTION_FORM = new Command(
                "Search for a", Command.ITEM, 3);
        private final Command CATEGORY_SEARCH = new Command("Category Search",
                Command.ITEM, 4);
        private final Command URL_POI = new Command("Get URL with all places",
                Command.ITEM, 4);
        private final Command URL_PLACE = new Command(
                "Get URL with closest place", Command.ITEM, 4);
        private final Command OK = new Command("Ok", Command.OK, 1);
        private PlaceLink currentPlace;
        private ResultPage page; // search results
        private final CategoryForm categoryForm;
        private final SuggestionForm suggestionForm;
        private final PlaceDetailsForm placeDetailsForm;
        private final FocalObserverComponent placeDataComponent;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);
        private final MapContainer resultsContainer;

        /**
         * @param display
         *            display is needed for event loop
         * @param midlet
         *            MIDlet is needed to exit the application
         */
        public PlaceDemo(Display display, MIDlet midlet) {
            // Force the example to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);
            // This will hold the result set
            resultsContainer = getMapFactory().createMapContainer();

            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 15));
            // The suggestions and categories should use the same location as a hint.
            categoryForm = new CategoryForm(BERLIN);
            suggestionForm = new SuggestionForm(BERLIN);
            placeDetailsForm = new PlaceDetailsForm(
                    HypertextLinkRunner.getInstance(midlet));

            addCommand(SHOW_SEARCH_SUGGESTION_FORM);
            addCommand(CATEGORY_SEARCH);

            placeDataComponent = new FocalObserverComponent(this);
            map.addMapComponent(placeDataComponent);
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
         * Generates an appropriate URL to share for the command
         *
         * @param c
         *            the button that was pressed.
         */
        private void handlePoiToUrl(Command c) {
            String generatedUrl = "";

            if (c == URL_POI) {
                GeoCoordinate[] coordinates = new GeoCoordinate[page.getItems().length];

                for (int i = 0; i < page.getItems().length; ++i) {
                    coordinates[i] = page.getItems()[i].getPosition();
                }
                generatedUrl = SharingManager.getInstance().getPoiUrl(
                        coordinates, MapSchemeType.NORMAL);
            } else if (c == URL_PLACE) {
                PlaceLink closest = null;
                GeoCoordinate center = map.getCenter();
                double minDistance = Double.MAX_VALUE;

                for (int i = 0; i < page.getItems().length; ++i) {
                    double currentDistance = center.distanceTo(
                            page.getItem(i).getPosition());

                    if (currentDistance < minDistance) {
                        minDistance = currentDistance;
                        closest = page.getItem(i);
                    }
                }
                generatedUrl = SharingManager.getInstance().getPlaceUrl(closest);
            } else if (c == PlaceDetailsForm.SHARE_THIS_PLACE) {
                generatedUrl = SharingManager.getInstance().getPlaceUrl(
                        currentPlace);

            }
            Alert a = new Alert("Generated URL", generatedUrl, null,
                    AlertType.INFO);

            a.setTimeout(Alert.FOREVER);
            display.setCurrent(a, this);
        }

        /**
         * Method to initiate a free text search.
         *
         * @param searchText
         *            the free text to look for within the current map view.
         */
        private void handleFreeTextSearchRequest(String searchText) {
            display.setCurrent(this);
            removeCommand(SHOW_SEARCH_SUGGESTION_FORM);
            removeCommand(CATEGORY_SEARCH);
            removeCommand(OK);
            progressStart("Searching place..", "Place was not found.");

            PlaceFactory sf = PlaceFactory.getInstance();
            ResultPageRequest placeSearchRequest = sf.createResultPageRequest();

            placeSearchRequest.search(searchText, map.getCenter(), this);
        }

        /**
         * Method to initiate a category based search.
         *
         * @param category
         *            The category to use to search for places.
         */
        private void handleCategorySearchRequest(Category category) {
            display.setCurrent(this);
            removeCommand(SHOW_SEARCH_SUGGESTION_FORM);
            removeCommand(CATEGORY_SEARCH);
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
         * @param placeLinks the places to add.
         */
        private void markResults(PlaceLink[] placeLinks) {
            map.removeMapObject(resultsContainer);
            resultsContainer.removeAllMapObjects();
            for (int i = 0; i < page.getItems().length; i++) {
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

            if (c == SHOW_PLACE_SUMMARY) {
                displayPlaceSummary();
            } else if (c == SHOW_PLACE_DETAILS) {
                placeDetailsForm.makePlaceRequest(currentPlace);
                placeDetailsForm.setCommandListener(getCommandListener());
                placeDetailsForm.addCommand(BACK);
                display.setCurrent(placeDetailsForm);

            } else if (c == SHOW_SEARCH_SUGGESTION_FORM) {

                suggestionForm.setSearchLocation(map.getCenter());
                suggestionForm.setCommandListener(getCommandListener());
                suggestionForm.addCommand(BACK);
                display.setCurrent(suggestionForm);

            } else if (c == SuggestionForm.FREE_TEXT_SEARCH) {
                placeDataComponent.clear();
                map.removeAllMapObjects();
                handleFreeTextSearchRequest(suggestionForm.getSearchText());
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
            } else {
                handlePoiToUrl(c);
            }
        }

        /**
         * Displays an alert showing information about a found place.
         */
        private void displayPlaceSummary() {

            StringBuffer summary = new StringBuffer();

            summary.append(currentPlace.getCategory().getTitle());
            summary.append("\n\n");
            summary.append(currentPlace.getVicinity().getText());

            Alert a = new Alert(currentPlace.getTitle(), summary.toString(),
                    currentPlace.getIcon(), AlertType.INFO);

            a.setTimeout(Alert.FOREVER);
            display.setCurrent(a, this);

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
            addCommand(SHOW_SEARCH_SUGGESTION_FORM);
            addCommand(CATEGORY_SEARCH);
            if (page.getItems().length > 0) {
                addCommand(URL_POI);
                addCommand(URL_PLACE);
                markResults(page.getItems());
                centerMapToFirstSearchResult();
                progressEnd();
            } else {
                removeCommand(URL_POI);
                removeCommand(URL_PLACE);
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
            addCommand(SHOW_SEARCH_SUGGESTION_FORM);
            addCommand(CATEGORY_SEARCH);
            removeCommand(URL_POI);
            removeCommand(URL_PLACE);
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
                removeCommand(SHOW_SEARCH_SUGGESTION_FORM);
                removeCommand(CATEGORY_SEARCH);
                removeCommand(URL_POI);
                removeCommand(URL_PLACE);
                addCommand(SHOW_PLACE_SUMMARY);
                addCommand(SHOW_PLACE_DETAILS);
            } else {
                removeCommand(SHOW_PLACE_SUMMARY);
                removeCommand(SHOW_PLACE_DETAILS);
                addCommand(SHOW_SEARCH_SUGGESTION_FORM);
                addCommand(CATEGORY_SEARCH);
                if (null != page && page.getItems().length > 0) {
                    addCommand(URL_POI);
                    addCommand(URL_PLACE);
                }
            }
        }

    }

}
