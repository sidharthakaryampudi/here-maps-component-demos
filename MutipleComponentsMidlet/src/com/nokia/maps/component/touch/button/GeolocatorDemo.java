package com.nokia.maps.component.touch.button;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.button.GeoLocatorButton;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.orientation.MapOrientator;

/**
 * Demonstrates adding a geolocator button to the map. When pressed, the app
 * will listen for GPS or Cell Id location events as appropriate.
 */
public class GeolocatorDemo extends MapCanvas implements CommandListener {

	private final Command EXIT = new Command("Exit", Command.EXIT, 1);

	private final MIDlet midlet;

	/**
	 * Constructor for the Geolocation Demo.
	 * @param display
	 * @param midlet
	 */
	public GeolocatorDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));

		// Add the button - the try catch is in case the Images fail to load.
		try {

			GeoLocatorButton locatorButton = new GeoLocatorButton(this);
			map.addMapComponent(locatorButton);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		// Set up the map.
		GeoBoundingBox europe = new GeoBoundingBox(
				new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));
		map.zoomTo(europe, false);

		this.midlet = midlet;

	}

	/**
	 * If the map overlay is unable to retrieve map tiles, or the emulator is
	 * unable to connect to the internet to verify the map copyrights, an error
	 * will be thrown here.
	 * 
	 * @param description
	 * @param detail
	 * @param critical
	 */
	public void onMapUpdateError(String description, Throwable detail,
			boolean critical) {
		// TODO Auto-generated method stub

		// This should be replaced by more comprehensive error handling..
		System.out.println(description);
		throw new RuntimeException(detail.getMessage());
	}

	/**
	 * This means that the all tiles are present and completely rendered with
	 * all objects present.
	 */
	public void onMapContentComplete() {
		// TODO Auto-generated method stub
	}

	/**
	 * Standard Command button interaction.
	 * 
	 * @param c
	 * @param d
	 */
	public void commandAction(final Command c, Displayable d) {
		if (c == EXIT) {
			midlet.notifyDestroyed();
		}
	}
}
