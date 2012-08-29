package com.nokia.maps.component.touch.button;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import com.nokia.location.GeoLocator;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.gui.ImageButtonRenderer;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;

public class GeoLocatorButton extends ButtonComponent implements
		LocationListener {

	public static final String ID = "geoLocator";

	private static final float THRESHOLD_DISTANCE = 100f;

	private final Image locatorOn;
	private final Image locatorOff;

	private boolean locating;

	private final MapStandardMarker youAreHereMarker;
	private final MapCanvas mapCanvas;

	private final GeoCoordinate currentLocation = new GeoCoordinate(0, 0,
			Float.NaN);

	/**
	 * Constructor for the Map Type Button.
	 * 
	 * @param display
	 * @param mapCanvas
	 * @throws IOException
	 */
	public GeoLocatorButton(MapCanvas mapCanvas) throws IOException {
		super(Graphics.TOP | Graphics.RIGHT, new ImageButtonRenderer(
				Image.createImage("/locate_off.png"),
				Image.createImage("/locate_e.png")));

		locatorOn = Image.createImage("/locate_on.png");
		locatorOff = Image.createImage("/locate_off.png");

		youAreHereMarker = mapCanvas.getMapFactory().createStandardMarker(
				currentLocation, 8, "", MapStandardMarker.HEXAGON);
		youAreHereMarker.setColor(0xAA008000);
		this.mapCanvas = mapCanvas;
	}

	/**
	 *  Toggles the geolocator.
	 */
	protected void touchAt(Point point) {
		locating = !locating;

		getImageGUI().setImage(locating ? locatorOn : locatorOff);
		if (locating) {
			GeoLocator.getInstance().setLocationListener(this);
		} else {
			GeoLocator.getInstance().setLocationListener(null);
			map.removeMapObject(youAreHereMarker);
		}
		super.touchAt(point);
	}

	private ImageButtonRenderer getImageGUI() {
		return ((ImageButtonRenderer) getRenderer());
	}

	public String getId() {
		return ID;
	}

	public void detach(MapDisplay map) {
		map.removeMapObject(youAreHereMarker);
		super.detach(map);
	}

	public void locationUpdated(LocationProvider provider, Location location) {

		currentLocation.setLatitude(location.getQualifiedCoordinates()
				.getLatitude());
		currentLocation.setLongitude(location.getQualifiedCoordinates()
				.getLongitude());
		currentLocation.setAltitude(location.getQualifiedCoordinates()
				.getAltitude());

		if (locating) {
			System.out.println(map.getCenter().distanceTo(currentLocation));
			if (map.getCenter().distanceTo(currentLocation) > THRESHOLD_DISTANCE) {
				youAreHereMarker.setCoordinate(currentLocation);
				map.removeMapObject(youAreHereMarker);
				map.addMapObject(youAreHereMarker);
				map.setCenter(currentLocation);
				// Ensure that the Map is refreshed with the new Map State.
				mapCanvas.onMapContentUpdated();
			}
		}

	}

	public void providerStateChanged(LocationProvider arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
