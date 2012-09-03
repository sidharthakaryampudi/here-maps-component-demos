package com.nokia.maps.example.routing;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.SideBarComponent;
import com.nokia.maps.component.touch.LongTouchComponent;
import com.nokia.maps.component.touch.TimerLongTouchComponent;
import com.nokia.maps.example.Base;
import com.nokia.maps.gesture.GestureHandler;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.maps.routing.Route;
import com.nokia.maps.routing.RouteFactory;
import com.nokia.maps.routing.RouteListener;
import com.nokia.maps.routing.RouteRequest;
import com.nokia.maps.routing.RoutingMode;
import com.nokia.maps.routing.TransportMode;
import com.nokia.maps.routing.WaypointParameterList;

/**
 * This class demonstrates RouteManager usage.
 */
public class RoutingDemo extends Base implements RouteListener {

	private final Command ADD = new Command("Add waypoint", Command.OK, 3);
	private final Command CALCULATE = new Command("Calculate route",
			Command.ITEM, 3);
	private final Command CALCULATE_WITH_MODE = new Command(
			"Calculate bicycle route ", Command.ITEM, 3);
	private final Command RESET = new Command("Reset", Command.ITEM, 3);
	// user selected coordinates
	private WaypointParameterList wpl = new WaypointParameterList();
	protected Route[] routes; // routing results

	private final LongTouchComponent longTouch;

	protected Image[] SELECTED_IMAGES = new Image[4];
	protected Image[] UNSELECTED_IMAGES = new Image[4];

	private static final String[] LABELS = new String[] { "  Car", "Cycle",
			" Walk", "Reset" };

	public RoutingDemo(Display display, MIDlet midlet) {
		super(display, midlet);

		try {
			UNSELECTED_IMAGES[0] = Image.createImage("/car.png");

			UNSELECTED_IMAGES[1] = Image.createImage("/cycle.png");
			UNSELECTED_IMAGES[2] = Image.createImage("/walk.png");
			UNSELECTED_IMAGES[3] = Image.createImage("/reset.png");

			SELECTED_IMAGES[0] = Image.createImage("/car_e.png");
			SELECTED_IMAGES[1] = Image.createImage("/cycle_e.png");
			SELECTED_IMAGES[2] = Image.createImage("/walk_e.png");
			SELECTED_IMAGES[3] = Image.createImage("/reset_e.png");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		map.setState(new MapDisplayState(new GeoCoordinate(51.477, 0.0, 0), 15));

		if (GestureHandler.init(this)) {
			longTouch = new LongTouchComponent(this, this, ADD);
		} else {
			longTouch = new TimerLongTouchComponent(this, this, ADD);
		}
		map.addMapComponent(longTouch);

		try {
			SideBarComponent sb = new SideBarComponent(this, this);
			sb.setCommands(SELECTED_IMAGES, UNSELECTED_IMAGES, LABELS,
					new Command[] { CALCULATE, CALCULATE_WITH_MODE,
							CALCULATE_WITH_MODE, RESET });
			map.addMapComponent(sb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds center coordinate to waypoint parameters list and adds marker to
	 * point out the location.
	 */
	private void addWaypoint(GeoCoordinate gc) {
		if (gc == null) {
			Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);

			gc = map.pixelToGeo(center);
		}
		wpl.addCoordinate(gc);
		map.addMapObject(mapFactory.createStandardMarker(gc, 10, null,
				MapStandardMarker.BALLOON));
	}

	/**
	 * Calculates route using waypoint parameters and adds polylines to
	 * visualize each route.
	 */
	private void calculateRoute(RoutingMode[] modes) {
		RouteFactory rf = RouteFactory.getInstance();

		rf.createRouteRequest().calculateRoute(wpl, modes, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Base#commandRun(javax.microedition.lcdui.Command)
	 */
	protected void commandRun(Command c) {
		if (c == ADD) {
			addCommand(RESET);
			addWaypoint(longTouch.getTouchAt());
			if (wpl.getWaypointCoordinates().length > 1) {
				addCommand(CALCULATE);
				addCommand(CALCULATE_WITH_MODE);
			}
		} else if (c == CALCULATE) {
			doRouting(new RoutingMode[] { new RoutingMode() });
		} else if (c == CALCULATE_WITH_MODE) {
			RoutingMode mode = new RoutingMode();

			mode.setTransportModes(new int[] { TransportMode.BICYCLE });
			RoutingMode[] modes = { mode };

			doRouting(modes);
		} else if (c == RESET) {
			removeCommand(RESET);
			addCommand(ADD);
			map.removeAllMapObjects();
			wpl = new WaypointParameterList();
			setTicker(null);
			routes = null;
		}
	}

	/**
	 * Executes routing with the given modes.
	 */
	private void doRouting(RoutingMode[] modes) {
		progressStart("Calculating route", "Route not available");
		addCommand(RESET);
		removeCommand(ADD);
		removeCommand(CALCULATE);
		removeCommand(CALCULATE_WITH_MODE);
		calculateRoute(modes);
	}

	/**
	 * Callback function initiated when a route request has successfully
	 * completed.
	 * 
	 * @param request
	 *            the route request that initiated the service call.
	 * @param routes
	 *            the routes found that fulfill the request.
	 */
	public void onRequestComplete(RouteRequest request, Route[] routes) {

		map.removeAllMapObjects();
		this.routes = routes;
		for (int i = 0; i < routes.length; i++) {
			map.addMapObject(mapFactory.createMapPolyline(routes[i].getShape(),
					3));
			map.setCenter(routes[i].getStart().getMappedPosition());
		}

		progressEnd();
	}

	/**
	 * Callback function should the Route Request fail for some reason.
	 * 
	 * @param request
	 *            the initiating route request.
	 * @param error
	 *            the reason the request failed.
	 */
	public void onRequestError(RouteRequest request, Throwable error) {
		map.removeAllMapObjects();
		error(error.toString());
	}
}
