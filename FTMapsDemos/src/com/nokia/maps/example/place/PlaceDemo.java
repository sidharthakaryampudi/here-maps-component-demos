package com.nokia.maps.example.place;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.common.Location;
import com.nokia.maps.common.Place;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.example.Base;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapSchemeType;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.maps.search.PlaceSearchRequest;
import com.nokia.maps.search.PlaceSearchRequestListener;
import com.nokia.maps.search.SearchFactory;
import com.nokia.maps.sharing.SharingManager;

/**
 * This demonstrates Place search usage.
 */
public class PlaceDemo extends Base implements PlaceSearchRequestListener,
		FocalEventListener {

	private final Command SHOW_PLACE_DETAILS = new Command("Details",
			Command.ITEM, 3);
	private final Command SHOW_PLACE_SUMMARY = new Command("Summary",
			Command.ITEM, 2);
	private final Command SEARCH = new Command("Search for a", Command.ITEM, 3);
	private final Command URL_POI = new Command("Get URL with all places",
			Command.ITEM, 4);
	private final Command URL_PLACE = new Command("Get URL with closest place",
			Command.ITEM, 4);
	private final Command OK = new Command("Ok", Command.OK, 1);
	private final TextBox searchBox = new TextBox("Enter search term", "Cafe",
			100, TextField.ANY);
	private Place currentPlace;
	private Place[] places; // search results

	private final FocalObserverComponent placeDataComponent;
	
	

	/**
	 * @param display
	 *            display is needed for event loop
	 * @param midlet
	 *            MIDlet is needed to exit the application
	 */
	public PlaceDemo(Display display, MIDlet midlet) {
		super(display, midlet);

		map.setState(new MapDisplayState(new GeoCoordinate(51.477, 0.0, 0), 15));

		addCommand(SEARCH);
		
		placeDataComponent = new FocalObserverComponent(this);
		map.addMapComponent(placeDataComponent);
		
	

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
			GeoCoordinate[] coordinates = new GeoCoordinate[places.length];

			for (int i = 0; i < places.length; ++i) {
				coordinates[i] = places[i].getLocations()[0]
						.getDisplayPosition();
			}
			generatedUrl = SharingManager.getInstance().getPoiUrl(coordinates,
					MapSchemeType.NORMAL);
		} else if (c == URL_PLACE) {
			// getPlaceUrl
			GeoCoordinate center = map.getCenter();
			double minDistance = Double.MAX_VALUE;
			Place closest = null;

			for (int i = 0; i < places.length; ++i) {
				Place current = places[i];

				Location[] currentLocations = current.getLocations();
				Location currentLocation = currentLocations[0];
				double currentDistance = center.distanceTo(currentLocation
						.getDisplayPosition());

				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					closest = current;
				}
			}
			generatedUrl = SharingManager.getInstance().getPlaceUrl(closest);
		}
		Alert a = new Alert("Generated URL", generatedUrl, null, AlertType.INFO);

		a.setTimeout(Alert.FOREVER);
		display.setCurrent(a, this);
	}

	/**
	 * Method to initiate the search.
	 */
	private void handleSearchRequest() {
		display.setCurrent(this);
		removeCommand(SEARCH);
		removeCommand(OK);
		progressStart("Searching place..", "Place was not found.");

		GeoCoordinate topLeft = map.pixelToGeo(new Point(0, 0));
		GeoCoordinate bottomRight = map.pixelToGeo(new Point(map.getWidth(),
				map.getHeight()));
		GeoBoundingBox bb = new GeoBoundingBox(topLeft, bottomRight);
		// This will not throw, as the listener will cause this to run in a
		// separate thread and this will catch the error.
		SearchFactory sf = SearchFactory.getInstance();
		PlaceSearchRequest placeSearchRequest = sf.createPlaceSearchRequest();

		placeSearchRequest.search(searchBox.getString(), new String[] {}, bb,
				this);
	}

	/**
	 * Method to add markers indicating the places returned in the search.
	 */
	private void markResults() {

		for (int i = 0; i < places.length; i++) {
			MapStandardMarker marker = mapFactory.createStandardMarker(
					places[i].getLocations()[0].getDisplayPosition(), 8,
					String.valueOf(i + 1), MapStandardMarker.BALLOON);

			map.addMapObject(marker);
			placeDataComponent.addData(marker, places[i]);
		}
	}

	/**
	 * Called from thread executing the command
	 * 
	 * @param c
	 *            Command being run
	 */
	protected void commandRun(Command c) {
		if (c == SHOW_PLACE_SUMMARY) {
			displayPlaceSummary();
		} else if (c == SHOW_PLACE_DETAILS) {
			displayPlaceDetails();
		} else if (c == SEARCH) {
			map.removeAllMapObjects();
			placeDataComponent.clear();
			centeringComponent.setFocusCommand(null);

			searchBox.addCommand(OK);
			searchBox.setCommandListener(this);
			display.setCurrent(searchBox);

		} else if (c == OK) {
			handleSearchRequest();
		} else {
			handlePoiToUrl(c);
		}
	}

	/**
	 * Shows information about a found place.
	 */
	private void displayPlaceSummary() {
		String name = currentPlace.getName();
		String text = "";

		if (currentPlace.getContact() != null) {
			if (null != currentPlace.getContact().getPhone()) {
				text += "Phone: " + currentPlace.getContact().getPhone() + "\n";
			}
			if (null != currentPlace.getContact().getEmail()) {
				text += "Email: " + currentPlace.getContact().getEmail() + "\n";
			}
			if (null != currentPlace.getContact().getUrl()) {
				text += "Url: " + currentPlace.getContact().getUrl();
			}
		} else {
			text += "No contact info.";
		}
		Alert a = new Alert(name, text, null, AlertType.INFO);

		a.setTimeout(Alert.FOREVER);
		display.setCurrent(a, this);

	}

	private void displayPlaceDetails() {
		try {
			midlet.platformRequest(SharingManager.getInstance().getPlaceUrl(
					currentPlace));
		} catch (ConnectionNotFoundException ce) {
			error(ce.toString());
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
	public void onRequestComplete(PlaceSearchRequest request, Place[] result) {
		places = result;
		addCommand(SEARCH);
		if (places.length > 0) {
			addCommand(URL_POI);
			addCommand(URL_PLACE);
			markResults();
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
	public void onRequestError(PlaceSearchRequest request, Throwable error) {
		places = null;
		addCommand(SEARCH);
		// removeCommand(URL_POI);
		// removeCommand(URL_PLACE);
		error(error.toString());
	}

	public void onFocusChanged(Object focus) {

		currentPlace = (Place) focus;

		if (currentPlace != null) {

			if (currentPlace.getContact() != null) {
				ChoiceGroup list1 = new ChoiceGroup(currentPlace.getName(),
						Choice.POPUP); // text, PopupList.LIST_DIALOG);
				list1.append(SHOW_PLACE_SUMMARY.getLabel(), null);
				list1.append(SHOW_PLACE_DETAILS.getLabel(), null);
				contextMenus.addData(list1, new Command[] {
						SHOW_PLACE_SUMMARY, SHOW_PLACE_DETAILS });
				//centeringComponent.setFocusCommand(null);
				infoBubble.clear();
			} else {
				infoBubble.addData(currentPlace.getName(), SHOW_PLACE_DETAILS);
				//centeringComponent.setFocusCommand(SHOW_PLACE_DETAILS);
				contextMenus.clear();
			}
		}
	}

}
