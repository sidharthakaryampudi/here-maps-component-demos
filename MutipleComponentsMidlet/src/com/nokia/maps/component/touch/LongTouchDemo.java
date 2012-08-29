package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.LongTouchComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.orientation.MapOrientator;

/**
 * Demonstrates the Long Touch component. When a Long Touch occurs,
 * the GeoCoordinate pressed is displayed in an alert on the screen.
 */
public class LongTouchDemo extends MapCanvas implements CommandListener {

	private final Command EXIT = new Command("Exit", Command.EXIT, 1);
	private final Command LONG_TOUCH = new Command("Touch", Command.OK, 3);

	private final LongTouchComponent longTouch;

	private final MIDlet midlet;

	/**
	 * Constructor for the Long Press Demo.
	 * @param display
	 * @param midlet
	 */
	public LongTouchDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));

		// Create the long Touch component. This class will handle the
		// LONG_TOUCH command.
		longTouch = new LongTouchComponent(this, this, LONG_TOUCH);
		map.addMapComponent(longTouch);

		// Set up the map.
		GeoBoundingBox world = new GeoBoundingBox(
				new GeoCoordinate(65, -90, 0), new GeoCoordinate(-65, 90, 0));
		map.zoomTo(world, false);

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
		} else if (c == LONG_TOUCH) {
			//  When a long Touch has occured, display the Geo Coordinates 
			// in an Alert.
			GeoCoordinate coord = longTouch.getTouchAt();
			double lat = (Math.floor(coord.getLatitude() * 10)) / 10d;
			double lng = (Math.floor(coord.getLongitude() * 10)) / 10d;
			Alert alertView = new Alert("Touch", "Touched at:" + Math.abs(lat)
					+ ((lat > 0) ? "N" : "S") + " " + Math.abs(lng)
					+ ((lng > 0) ? "E" : "W"), null, AlertType.ERROR);
			display.setCurrent(alertView);
		}
	}
}
