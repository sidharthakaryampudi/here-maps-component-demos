package com.nokia.maps.example.routing;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.LongTouchComponent;
import com.nokia.maps.example.Base;
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

    public RoutingDemo(Display display, MIDlet midlet) {
        super(display, midlet);

        map.setState(new MapDisplayState(new GeoCoordinate(51.477, 0.0, 0), 15));
        // addCommand(ADD);
        
        longTouch = new  LongTouchComponent(this, this, ADD);
        map.addMapComponent(longTouch);
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
        map.addMapObject(
                mapFactory.createStandardMarker(gc, 10, null,
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
            doRouting(new RoutingMode[] { new RoutingMode()});
        } else if (c == CALCULATE_WITH_MODE) {
            RoutingMode mode = new RoutingMode();

            mode.setTransportModes(new int[] { TransportMode.BICYCLE});
            RoutingMode[] modes = { mode};

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
     * Callback function initiated when a route request has successfully completed.
     * @param request the route request that initiated the service call.
     * @param routes  the routes found that fulfill the request.
     */
    public void onRequestComplete(RouteRequest request, Route[] routes) {

        map.removeAllMapObjects();
        this.routes = routes;
        for (int i = 0; i < routes.length; i++) {
            map.addMapObject(
                    mapFactory.createMapPolyline(routes[i].getShape(), 3));
            map.setCenter(routes[i].getStart().getMappedPosition());
        }

        progressEnd();
    }

    /**
     * Callback function should the Route Request fail for some reason.
     * @param request the initiating route request.
     * @param error the reason the request failed.
     */
    public void onRequestError(RouteRequest request, Throwable error) {
        map.removeAllMapObjects();
        error(error.toString());
    }
}
