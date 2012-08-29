/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.feedback;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapListener;
import com.nokia.maps.map.Point;

/**
 * 
 * View Restriction Component blanks out the Map beyond a given boundary.
 * 
 * This is an example of a Map Component which acts as an observer on the state
 * of the attached map. The user is unable to view, pan or scale beyond a
 * defined area of the map.
 */
public class RestrictMapComponent implements MapComponent {

	public static final String ID = "Restrict";

	/**
	 * Look-and-feel of the Restricted Area.
	 */
	private static final int MAP_FOG_COLOR = 0xA0A0A0;
	private static final int BORDER_WIDTH = 50;

	/**
	 * On Screen representation of the boundary is in Pixels.
	 */
	private Point boundaryBottomRight;
	private Point boundaryTopLeft;
	private static final Point NOKIA_LOGO = new Point(50, 9);

	private static final int MAX_LATITUDE = 85;
	private static final int MAX_LONGITUDE = 180;
	public static final int UNRESTRICTED = -1;
	private double maxZoomLevel = UNRESTRICTED;
	private double minZoomLevel = UNRESTRICTED;
	/**
	 * Calculated Restricted Boundary is Geographic
	 */
	private GeoBoundingBox boundingBox;

	private final MapListener mapListener;
	private final Display display;

	private MapDisplay map;

	/**
	 * Main Component Constructor
	 * 
	 * @param mapListener
	 *            required since the component can alter the state of the map
	 *            and therefore needs to be able to request a repaint.
	 * @param display
	 *            required since the component gives audible feedback
	 * @param boundingBox
	 *            the region to restrict the view to.
	 */
	public RestrictMapComponent(MapListener mapListener, Display display,
			GeoBoundingBox boundingBox) {

		this.mapListener = mapListener;
		this.display = display;
		setBoundingBox(boundingBox);

	}

	/**
	 * Constructor which sets a default boundary to the whole world.
	 * 
	 * @param mapListener
	 *            required since the component can alter the state of the map
	 *            and therefore needs to be able to request a repaint.
	 * @param display
	 *            required since the component gives audible feedback
	 */
	public RestrictMapComponent(MapListener mapListener, Display display) {
		this(mapListener, display, new GeoBoundingBox(new GeoCoordinate(
				MAX_LATITUDE, -MAX_LONGITUDE, Float.NaN), new GeoCoordinate(
				-MAX_LATITUDE, MAX_LONGITUDE, Float.NaN)));
	}

	// from MapComponent
	public String getId() {
		return ID;
	}

	// from MapComponent
	public String getVersion() {
		return "1.0";
	}

	/**
	 * This map components does not respond to user events, and therefore does
	 * not need an event listener.
	 */
	public EventListener getEventListener() {
		return null;
	}

	/**
	 * To give visual feedback on the screen, need to use the low level graphics
	 * API here. Note that the code in the paint method is dumb, all required
	 * calculations have already been made when mapUpdated was last called.
	 */
	public void paint(Graphics g) {

		if (boundaryTopLeft != null) {
			g.setColor(MAP_FOG_COLOR);
			if (boundaryTopLeft.getX() > 0) {
				int bar = Math.min(boundaryTopLeft.getX(), NOKIA_LOGO.getX());
				g.fillRect(0, 0, bar, map.getHeight() - NOKIA_LOGO.getY());
				if (boundaryTopLeft.getX() > NOKIA_LOGO.getX()) {
					g.fillRect(NOKIA_LOGO.getX(), 0, boundaryTopLeft.getX()
							- NOKIA_LOGO.getX(), map.getHeight());
				}
			}
			if (boundaryTopLeft.getY() > 0) {
				g.fillRect(0, 0, map.getWidth(), boundaryTopLeft.getY());
			}

			if (boundaryBottomRight.getX() < map.getWidth()) {
				g.fillRect(boundaryBottomRight.getX(), 0, map.getWidth()
						- boundaryBottomRight.getX(), map.getHeight());
			}

			if (boundaryBottomRight.getY() < map.getHeight()) {
				int bar = Math.max(boundaryBottomRight.getY(), map.getHeight()
						- NOKIA_LOGO.getY());
				g.fillRect(NOKIA_LOGO.getX(), bar,
						map.getWidth() - NOKIA_LOGO.getX(), map.getHeight()
								- bar);
				if (boundaryBottomRight.getY() < map.getHeight()
						- NOKIA_LOGO.getY()) {
					g.fillRect(0, boundaryBottomRight.getY(), map.getWidth(),
							map.getHeight() - boundaryBottomRight.getY()
									- NOKIA_LOGO.getY());
				}
			}
		} 

	}

	/**
	 * This method is called whenever the state of the map is altered. For the
	 * restrict component, there are two parts to this, observation of the map
	 * state (including necessary correction and feedback) and calculation of
	 * the restriction boundary.
	 * 
	 * @param zoomChanged
	 *            whether the zoom level has been changed in the last map
	 *            update.
	 */
	public void mapUpdated(boolean zoomChanged) {
		calculateBoundary();
		checkValidity(zoomChanged);
	}

	/**
	 * Definition of the area of the MapCanvas to blank out, based on the
	 * geographic area currently displayed on screen.
	 */
	private void calculateBoundary() {
		boundaryBottomRight = map.geoToPixel(boundingBox.getBottomRight());
		boundaryTopLeft = map.geoToPixel(boundingBox.getTopLeft());
		boundaryBottomRight.translate(BORDER_WIDTH, BORDER_WIDTH);
		boundaryTopLeft.translate(-BORDER_WIDTH, -BORDER_WIDTH);
	}

	/**
	 * Observation and correction of the MapState based on the restrictions
	 * currently in place.
	 * 
	 * @param zoomChanged
	 *            whether the zoom level has been changed in the last map
	 *            update.
	 */
	private void checkValidity(boolean zoomChanged) {
		GeoCoordinate center = new GeoCoordinate(map.getCenter());
		double zoomLevel = map.getZoomLevel();
		boolean invalid = false; // Keep tract of whether a correction needs to
									// be made.

		if (zoomChanged) {
			// Only check if the zoom level has been changed.
			if (getMaxZoomLevel() != UNRESTRICTED
					&& map.getZoomLevel() > maxZoomLevel) {
				zoomLevel = maxZoomLevel;
				invalid = true;
			} else if (getMinZoomLevel() != UNRESTRICTED
					&& map.getZoomLevel() < minZoomLevel) {
				zoomLevel = minZoomLevel;
				invalid = true;
			}
		}

		if (boundingBox != null && boundingBox.isValid()
				&& !boundingBox.contains(map.getCenter())) {
			// The central point of the map is outside of the restricted
			// Area and needs correction.
			restrictCenter(center);
			invalid = true;
		}

		if (invalid) {
			// Assuming the current state of the map is invalid,
			// Play an audible warning and correct the Mpa State.
			AlertType.WARNING.playSound(display);
			map.setState(new MapDisplayState(center, zoomLevel));
			// Ensure that the Map is refreshed with the new Map State.
			mapListener.onMapContentUpdated();
		}
	}

	/**
	 * Update the center of the map to lie within the restricted boundary.
	 * 
	 * @param center
	 *            the center of the map.
	 */
	private void restrictCenter(GeoCoordinate center) {
		if (center.getLatitude() < boundingBox.getBottomRight().getLatitude()) {
			center.setLatitude(boundingBox.getBottomRight().getLatitude());
		}
		if (center.getLatitude() > boundingBox.getTopLeft().getLatitude()) {
			center.setLatitude(boundingBox.getTopLeft().getLatitude());
		}
		if (center.getLongitude() < boundingBox.getTopLeft().getLongitude()) {
			center.setLongitude(boundingBox.getTopLeft().getLongitude());
		}
		if (center.getLongitude() > boundingBox.getBottomRight().getLongitude()) {
			center.setLongitude(boundingBox.getBottomRight().getLongitude());
		}
	}

	/**
	 * This method is called whenever the map component is attached to a new
	 * MapDisplay. Assuming a bounding box has been set, update the current map
	 * state to force the map to lie within the defined boundary.
	 */
	public void attach(MapDisplay map) {
		this.map = map; // Obtain a reference to the current map.
		if (boundingBox != null) {
			if (boundingBox.isValid() && !boundingBox.contains(map.getCenter())) {
				map.zoomTo(boundingBox, false);
			}
			// The minimum zoom level will display the restricted zone on
			// screen.
			minZoomLevel = (minZoomLevel < map.getZoomLevel()) ? map
					.getZoomLevel() : minZoomLevel;
		}
	}

	/**
	 * This method is called whenever the map component is removed form the
	 * display. The usual housekeeping/tidy up functions occur here.
	 */
	public void detach(MapDisplay map) {
		this.map = null; // remove the map reference.
	}

	/**
	 * 
	 * @return
	 */
	private double getMaxZoomLevel() {
		return maxZoomLevel;
	}

	/**
	 * 
	 * @param maxZoomLevel
	 */
	public void setMaxZoomLevel(double maxZoomLevel) {
		this.maxZoomLevel = maxZoomLevel;
	}

	/**
	 * 
	 * @return
	 */
	public double getMinZoomLevel() {
		return minZoomLevel;
	}

	/**
	 * 
	 * @param minZoomLevel
	 */
	public void setMinZoomLevel(double minZoomLevel) {
		this.minZoomLevel = minZoomLevel;
	}

	/**
	 * 
	 * @return
	 */
	public GeoBoundingBox getBoundingBox() {
		return boundingBox;
	}

	/**
	 * 
	 * @param boundingBox
	 */
	public void setBoundingBox(GeoBoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

}
