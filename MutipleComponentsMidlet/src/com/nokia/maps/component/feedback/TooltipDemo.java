package com.nokia.maps.component.feedback;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.orientation.MapOrientator;

/**
 * Demonstrates adding a series of tooltips to map markers. When a Map Marker is
 * centered on the screen, a tooltip appears below it. Unlike Infobubbles, the
 * tooltips do not react to Touch Events.
 */
public class TooltipDemo extends MapCanvas implements CommandListener,
		FocalEventListener {

	private final Command EXIT = new Command("Exit", Command.EXIT, 1);

	private final TooltipComponent tooltips;
	private final FocalObserverComponent focalComponent;

	private final MIDlet midlet;

	/**
	 * Constructor for the Tooltip Demo.
	 * 
	 * @param display
	 * @param midlet
	 */
	public TooltipDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));
		map.addMapComponent(new CenteringComponent(this));

		// Add the tooltip component FIRST so that it is processed LAST.
		tooltips = new TooltipComponent();
		map.addMapComponent(tooltips);

		// Add the Focal component SECOND to 
		focalComponent = new FocalObserverComponent(this);
		map.addMapComponent(focalComponent);

		// Now we can set up the markers..
		addMarkerData(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
		addMarkerData(
				new GeoCoordinate(51.477811d, -0.001475d, (float) Float.NaN),
				"London, this is the city where the Olympic Games took place in the summer of 2012.");
		addMarkerData(new GeoCoordinate(60.170833, 24.9375, (float) Float.NaN),
				"Helsinki");
		addMarkerData(
				new GeoCoordinate(59.949444, 10.756389, (float) Float.NaN),
				"Oslo");

		addMarkerData(new GeoCoordinate(45.4375, 12.335833, (float) Float.NaN),
				"Venice");

		// Set up the map.
		GeoBoundingBox europe = new GeoBoundingBox(
				new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));
		map.zoomTo(europe, false);

		// Ensure that the Zoom Buttons are at the back of the display queue.
		MapComponent component = map.getMapComponent("ZoomImgComponent");
		map.removeMapComponent(component);
		map.addMapComponent(component);
		this.midlet = midlet;

	}

	/**
	 * Helper function to add markers and prime the Focal Observer with data.
	 * 
	 * @param coord
	 * @param text
	 */
	private void addMarkerData(GeoCoordinate coord, String text) {
		MapStandardMarker marker = mapFactory.createStandardMarker(coord);
		focalComponent.addData(marker, text);

		map.addMapObject(marker);
	}

	/**
	 * The callback from the focal observer sets up the Tooltip text.
	 */
	public void onFocusChanged(Object focus) {

		String currentFocus = (String) focus;
		if (currentFocus != null) {
			tooltips.add(currentFocus);
		} else {
			tooltips.clear();
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
