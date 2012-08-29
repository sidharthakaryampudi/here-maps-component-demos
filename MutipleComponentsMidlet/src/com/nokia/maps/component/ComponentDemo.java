package com.nokia.maps.component;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.ContextMenuComponent;
import com.nokia.maps.component.touch.button.GeoLocatorButton;
import com.nokia.maps.component.touch.button.MapTypeButton;
import com.nokia.maps.component.touch.button.PictureInPictureButton;
import com.nokia.maps.component.touch.button.ScaleBarButton;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.orientation.MapOrientator;
import com.nokia.maps.selector.MapTypeSelector;

/**
 * Demonstrates adding a variety of Custom Components.
 */
public class ComponentDemo extends MapCanvas implements CommandListener {

	private final Command EXIT = new Command("Exit", Command.EXIT, 1);

	private final TooltipComponent tooltips;
	private final ContextMenuComponent contextMenus;

	private final MIDlet midlet;

	private final Command DO1 = new Command("1", Command.ITEM, 1);
	private final Command DO2 = new Command("2", Command.ITEM, 2);
	private final Command DO3 = new Command("3", Command.ITEM, 3);
	private final Command DO4 = new Command("4", Command.ITEM, 4);
	private final Command DO5 = new Command("5", Command.ITEM, 5);
	private final Command DO6 = new Command("6", Command.ITEM, 6);
	private Command[] commands = { DO1, DO2, DO3, DO4, DO5, DO6 };

	/**
	 * Constructor for the Map Component Demo.
	 * @param display
	 * @param midlet
	 */
	public ComponentDemo(Display display, MIDlet midlet) {
		super(display);

		addCommand(EXIT);
		setCommandListener(this);

		// Allows landscape or Portrait Mode where applicable.
		MapOrientator.init(midlet);

		// Removes unnecessary map components.
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));

		// Set up the Tooltip and Context Menu components FIRST.
		tooltips = new TooltipComponent();
		map.addMapComponent(tooltips);

		contextMenus = new ContextMenuComponent(this, this);
		map.addMapComponent(contextMenus);

		// Set up the Map Marker centerer afterwards, so that the
		// centerer fires first and the tooltips and context menus are
		// handled afterwards.
		map.addMapComponent(new CenteringComponent(this));

		// Set up a variety of buttons on screen.
		setUpButtons();

		// Add four tooltips and a context menu.
		// The data can be add in directly here, or it could be provided
		// by a focal observer.
		addCityMarker(new GeoCoordinate(40.4, -3.683333, (float) Float.NaN),
				"Madrid");
		addCityMarker(
				new GeoCoordinate(51.477811d, -0.001475d, (float) Float.NaN),
				"London, this is the city where the Olympic Games took place in the summer of 2012.");
		addCityMarker(new GeoCoordinate(60.170833, 24.9375, (float) Float.NaN),
				"Helsinki");
		addCityMarker(
				new GeoCoordinate(59.949444, 10.756389, (float) Float.NaN),
				"Oslo");

		addCityMarkerMenu(new GeoCoordinate(45.4375, 12.335833,
				(float) Float.NaN), "Venice");

		// Set up the map.
		GeoBoundingBox europe = new GeoBoundingBox(
				new GeoCoordinate(65, -5, 0), new GeoCoordinate(35, 15, 0));
		map.zoomTo(europe, false);

		// Ensure that the Zoom Buttons are at the back of the display queue.
		// e.g. behind the context menu.
		MapComponent component = map.getMapComponent("ZoomImgComponent");
		map.removeMapComponent(component);
		map.addMapComponent(component);
		this.midlet = midlet;

	}

	private void setUpButtons() {

		// Add a mapType Component, Geolocator and Pic-in-Pic.
		try {
			MapTypeButton mapTypeButton = new MapTypeButton(display, this);
			map.addMapComponent(mapTypeButton);

			GeoLocatorButton locatorButton = new GeoLocatorButton(this);
			locatorButton.setOffset(40, 10);
			map.addMapComponent(locatorButton);

			PictureInPictureButton picInPicButton = new PictureInPictureButton();
			map.addMapComponent(picInPicButton);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		// Add a scale bar component.
		ScaleBarButton scale = new ScaleBarButton(Graphics.BOTTOM
				| Graphics.LEFT);
		scale.setOffset(10, 30);
		map.addMapComponent(scale);

	}

	/**
	 * Helper function to add tooltip texts.
	 * 
	 * @param coord
	 * @param text
	 */
	private void addCityMarker(GeoCoordinate coord, String text) {
		MapStandardMarker marker = mapFactory.createStandardMarker(coord);
		tooltips.add(marker, text);

		map.addMapObject(marker);
	}

	/**
	 * Helper function to add in a Context menu.
	 * 
	 * @param coord
	 * @param text
	 */
	private void addCityMarkerMenu(GeoCoordinate coord, String text) {
		MapStandardMarker marker = mapFactory.createStandardMarker(coord);
		ChoiceGroup list1 = new ChoiceGroup(text, Choice.POPUP);
		list1.append("item1", null);
		list1.append("item2", null);
		list1.append("item3", null);
		list1.append("item4", null);
		list1.append("item5", null);
		list1.append("item6", null);
		list1.append("item7", null);
		list1.append("item8", null);

		contextMenus.addData(marker, list1, commands);

		map.addMapObject(marker);
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
		} else if (c == DO1) {
			doAlert(c);
		} else if (c == DO2) {
			doAlert(c);
		} else if (c == DO3) {
			doAlert(c);
		} else if (c == DO4) {
			doAlert(c);
		} else if (c == DO5) {
			doAlert(c);
		} else if (c == DO6) {
			doAlert(c);
		}
		// Handles The IconCommand.BACK action if pressed.
		MapTypeSelector.handleCommandAction(c, d);
	}

	/**
	 * Put something up on screen since a context menu has been pressed.
	 * @param c
	 */
	private void doAlert(Command c) {
		Alert alert = new Alert("");
		alert.setTimeout(1000);
		alert.setString(c.getLabel() + " was pressed");
		display.setCurrent(alert);

	}
}
