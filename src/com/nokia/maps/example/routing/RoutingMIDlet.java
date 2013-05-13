package com.nokia.maps.example.routing;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapPolyline;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;
import com.nokia.maps.routing.Mode;
import com.nokia.maps.routing.Route;
import com.nokia.maps.routing.RouteFactory;
import com.nokia.maps.routing.RouteListener;
import com.nokia.maps.routing.RouteRequest;
import com.nokia.maps.routing.WaypointParameterList;
import com.nokia.maps.routing.enums.RoutingType;
import com.nokia.maps.routing.enums.TransportMode;


/**
 * This MIDlet just sets example as current Displayable.
 */
public class RoutingMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new RoutingDemo(display, this);
    }

    protected String getTitle() {
        return "Routing Demo";
    }

    protected String getDescription() {
        return "This example shows how a route manager can be used to calculate a route between two waypoints and "
                + " display the  result on the map. The Routing manager supports various options in making a routing request like setting type,"
                + " transport mode and routes to avoid.\nDetailed turn-by-turn instructions are also available.";
    }

    /**
     * This class demonstrates RouteManager usage.
     */
    private class RoutingDemo extends MapCanvasExample implements RouteListener {

        private final Command ADD = new Command("Add waypoint", Command.OK, 3);
        private final Command CALCULATE = new Command("Calculate route",
                Command.ITEM, 3);
        private final Command CALCULATE_WITH_MODE = new Command(
                "Calculate walking route ", Command.ITEM, 3);
        private final Command RESET = new Command("Reset", Command.ITEM, 3);
        private final Command SHOW_MANUEVERS = new Command("Turn-by-turn",
                Command.ITEM, 4);
        // user selected coordinates
        private final WaypointParameterList wpl = new WaypointParameterList();
        private Mode mode;
        protected Route[] routes; // routing results
        private TurnByTurnForm turnByTurn;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public RoutingDemo(Display display, MIDlet midlet) {
            // Displaying polylines can require a lot of heap memory.
            // Force the example to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));
            addCommand(ADD);
        }

        /**
         * Adds center coordinate to waypoint parameters list and adds marker to
         * point out the location.
         */
        private void addWaypoint() {
            Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);
            GeoCoordinate gc = map.pixelToGeo(center);

            wpl.addCoordinate(gc);
            map.addMapObject(
                    mapFactory.createStandardMarker(gc, 10, null,
                    MapStandardMarker.BALLOON));
        }

        /*
         * (non-Javadoc)
         *
         * @see Base#commandRun(javax.microedition.lcdui.Command)
         */
        public void commandRun(Command c) {
            if (c == ADD) {
                addCommand(RESET);
                addWaypoint();
                if (wpl.getWaypointCoordinates().length > 1) {
                    addCommand(CALCULATE);
                    addCommand(CALCULATE_WITH_MODE);
                }
            } else if (c == CALCULATE) {

                mode = new Mode();
                mode.setRoutingType(RoutingType.FASTEST);
                makeRoutingRequest(mode);
            } else if (c == CALCULATE_WITH_MODE) {
                mode = new Mode();
                mode.setTransportModes(new int[] { TransportMode.PEDESTRIAN});
                makeRoutingRequest(mode);
            } else if (c == RESET) {
                removeCommand(RESET);
                removeCommand(SHOW_MANUEVERS);
                addCommand(ADD);
                map.removeAllMapObjects();
                wpl.clear();
                setTicker(null);
                routes = null;
            } else if (c == SHOW_MANUEVERS) {
                turnByTurn = new TurnByTurnForm(routes[0]);
                turnByTurn.setCommandListener(getCommandListener());
                display.setCurrent(turnByTurn);

            } else if (c == TurnByTurnForm.BACK) {
                display.setCurrent(this);
            }
        }

        /**
         * Executes routing with the given modes.
         * @param modes the routing mode to use.
         */
        private void makeRoutingRequest(Mode modes) {
            progressStart("Calculating route", "Route not available");
            addCommand(RESET);
            removeCommand(ADD);
            removeCommand(CALCULATE);
            removeCommand(CALCULATE_WITH_MODE);
            addCommand(SHOW_MANUEVERS);

            RouteFactory rf = RouteFactory.getInstance();

            rf.createRouteRequest().calculateRoute(wpl, modes, this);
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
                MapPolyline line = mapFactory.createMapPolyline(
                        routes[i].getShape(), 2);

                line.setColor(0xa00000ff);
                map.addMapObject(line);

                map.addMapObject(
                        mapFactory.createStandardMarker(
                                routes[i].getStart().getMappedPosition(), 10,
                                "A", MapStandardMarker.BALLOON));

                map.addMapObject(
                        mapFactory.createStandardMarker(
                                routes[i].getDestination().getMappedPosition(),
                                10, "B", MapStandardMarker.BALLOON));

                map.zoomTo(routes[i].getBoundingBox(), false);
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
}
