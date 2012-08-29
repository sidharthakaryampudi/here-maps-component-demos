package com.nokia.maps.component.feedback;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.orientation.MapOrientator;

/**
 * Demonstrates adding map marker and moving it.
 */
public class FocalObserverDemo extends MapCanvas implements CommandListener,
		FocalEventListener {

	private final Command EXIT = new Command("Exit", Command.EXIT, 1);

	private final FocalObserverComponent focalObserver;

	private final MIDlet midlet;

	public FocalObserverDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

		focalObserver = new FocalObserverComponent(this);
		map.addMapComponent(focalObserver);

		addCityMarker(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
		addCityMarker(new GeoCoordinate(51.477811d, -0.001475d, 0), "London");
		addCityMarker(new GeoCoordinate(60.170833, 24.9375, 0), "Helsinki");
		addCityMarker(new GeoCoordinate(59.949444, 10.756389, 0), "Oslo");

		addCityMarker(new GeoCoordinate(45.4375, 12.335833, 0), "Venice");

		GeoBoundingBox europe = new GeoBoundingBox(
				new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));
		map.zoomTo(europe, false);

		// Ensure that the Zoom Buttons are at the back of the display queue.
		MapComponent component = map.getMapComponent("ZoomImgComponent");
		map.removeMapComponent(component);
		map.addMapComponent(component);
		this.midlet = midlet;

	}

	private void addCityMarker(GeoCoordinate coord, String text) {
		MapStandardMarker marker = mapFactory.createStandardMarker(coord);
		focalObserver.addData(marker, text);
		map.addMapObject(marker);
	}

	public void onFocusChanged(Object focus) {

		if (focus != null) {

			Alert alertView = new Alert("Focus on ", (String) focus, null,
					AlertType.ERROR);
			display.setCurrent(alertView);
		}

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
