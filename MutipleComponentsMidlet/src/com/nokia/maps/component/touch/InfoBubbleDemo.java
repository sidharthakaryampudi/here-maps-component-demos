package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.InfoBubbleComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.orientation.MapOrientator;

/**
 * Demonstrates adding a series of Infobubbles to the map. When a Map Marker is
 * centered on the screen, it is replaced by an Infobubble. Unlike a tooltip,
 * an infobubble is itself clickable, and can also be scrolled if the text
 * is too long.
 */
public class InfoBubbleDemo extends MapCanvas implements CommandListener,
		FocalEventListener {

	private static final Command EXIT = new Command("Exit", Command.EXIT, 1);
	private static final Command BUBBLE = new Command("InfoBubble", Command.OK,
			1);

	private final MIDlet midlet;
	
	private final InfoBubbleComponent infoBubble;
	private final FocalObserverComponent focalComponent;
	private final Alert alert;

	private String currentFocus;

	
	/**
	 * Constructor for the Infobubble Demo.
	 * @param display
	 * @param midlet
	 */
	
	public InfoBubbleDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);
		alert = new Alert("");
		alert.setTimeout(1000);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		// the default cursor will be unnecessary since the Infobubble
		// will be providing visual feedback.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));
		map.addMapComponent(new CenteringComponent(this));

		// Add the infobubble component FIRST so that it is processed LAST.
		infoBubble = new InfoBubbleComponent(this, this);
		map.addMapComponent(infoBubble);

		// Add the Focal component SECOND to feed the info bubble component ABOVE.
		focalComponent = new FocalObserverComponent(this);
		map.addMapComponent(focalComponent);

		// Finally add the Centering Component to feed the focal observer
		map.addMapComponent(new CenteringComponent(this));

		// Now we can set up the markers..
		addMarkerData(new GeoCoordinate(40.4, -3.683333, 0),
				"Madrid");
		addMarkerData(
				new GeoCoordinate(51.477811d, -0.001475d, 0),
				"London, this is the city where the Olympic Games took place in the summer of 2012."
						+ "The 2012 Summer Olympics, officially the Games of the XXX Olympiad, and also more generally known as London 2012, was a major international"
						+ " multi-sport event, celebrated in the tradition of the Olympic Games, as governed by the International Olympic Committee (IOC), that took place in"
						+ " London, United Kingdom, from 27 July to 12 August 2012. The first event, the group stages in women's football, began two days earlier, on 25 July."
						+ " More than 10,000 athletes from 204 National Olympic Committees (NOCs) participated."
						+ "Following a bid headed by former Olympic champion Sebastian Coe and then-Mayor of London Ken Livingstone, London was selected as the host city on "
						+ "6 July 2005 during the 117th IOC Session in Singapore, defeating bids from Moscow, New York City, Madrid and Paris."
						+ "London was the first city to officially host the modern Olympic Games three times, having previously done so in 1908 and in 1948.");
		addMarkerData(new GeoCoordinate(60.170833, 24.9375, 0),
				"Helsinki");
		addMarkerData(
				new GeoCoordinate(59.949444, 10.756389, 0),
				"Oslo");

		addMarkerData(new GeoCoordinate(45.4375, 12.335833, 0),
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
	 * @param coord
	 * @param text
	 */
	private void addMarkerData(GeoCoordinate coord, String text) {
		MapStandardMarker marker = mapFactory.createStandardMarker(coord);
		focalComponent.addData(marker, text);

		map.addMapObject(marker);
	}

	/**
	 * The callback from the focal observer sets up the Infobubble.
	 */
	public void onFocusChanged(Object focus) {

		currentFocus = (String) focus;
		if (currentFocus != null) {
			infoBubble.addData(currentFocus, BUBBLE);
		} else {
			infoBubble.clear();
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
		} else if (c == BUBBLE) {
			// The infobubble has been clicked.
			alert.setString(currentFocus + " " + c.getLabel() + " was pressed");
			display.setCurrent(alert);
		}
	}
}
