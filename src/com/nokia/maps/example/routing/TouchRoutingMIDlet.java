package com.nokia.maps.example.routing;


import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.gesture.GestureLongTouch;
import com.nokia.maps.component.gesture.LongTouchComponent;
import com.nokia.maps.component.gesture.TimerLongTouch;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.GestureMapCanvasExample;
import com.nokia.maps.example.component.touch.SideBarComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.routing.Mode;
import com.nokia.maps.routing.Route;
import com.nokia.maps.routing.RouteFactory;
import com.nokia.maps.routing.RouteListener;
import com.nokia.maps.routing.RouteRequest;
import com.nokia.maps.routing.WaypointParameterList;
import com.nokia.maps.routing.enums.TransportMode;
import com.nokia.maps.ui.helpers.Orientation;


/**
 * This MIDlet just sets example as current Displayable.
 */
public class TouchRoutingMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new TouchRoutingDemo(display, this);

    }

    protected String getTitle() {
        return "Touch Routing Demo";
    }

    protected String getDescription() {
        return "The routing demo has been optimised for touch "
                + " by adding centering and longpress components. "
                + "The Commands are accessed though the use of a Sidebar.";
    }

    /**
     * This class demonstrates RouteManager usage.
     */
    private class TouchRoutingDemo extends GestureMapCanvasExample implements RouteListener {

        private final Command ADD = new Command("Add waypoint", Command.OK, 3);
        private final Command CALCULATE = new Command("Calculate route",
                Command.ITEM, 3);
        private final Command CALCULATE_WITH_MODE = new Command(
                "Calculate bicycle route ", Command.ITEM, 3);
        private final Command RESET = new Command("Reset", Command.ITEM, 3);
        private final Command SHOW_MANUEVERS = new Command("Turn-by-turn",
                Command.ITEM, 4);
        // user selected coordinates
        private WaypointParameterList wpl = new WaypointParameterList();
        protected Route[] routes; // routing results
        private Mode mode;
        private TurnByTurnForm turnByTurn;
        private final CenteringComponent centeringComponent;
        private final LongTouchComponent longTouch;
        protected Image[] SELECTED_IMAGES = new Image[4];
        protected Image[] UNSELECTED_IMAGES = new Image[4];
        private final String[] LABELS = new String[] {
            "  Car", "Cycle", " Walk", "Reset"};
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public TouchRoutingDemo(Display display, MIDlet midlet) {
            // Displaying polylines can require a lot of heap memory.
            // Force the example to use the smaller tile size to reduce the heap requirement.
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);

            try {
                UNSELECTED_IMAGES[0] = Image.createImage("/route/car.png");

                UNSELECTED_IMAGES[1] = Image.createImage("/route/cycle.png");
                UNSELECTED_IMAGES[2] = Image.createImage("/route/walk.png");
                UNSELECTED_IMAGES[3] = Image.createImage("/route/reset.png");

                SELECTED_IMAGES[0] = Image.createImage("/route/car_e.png");
                SELECTED_IMAGES[1] = Image.createImage("/route/cycle_e.png");
                SELECTED_IMAGES[2] = Image.createImage("/route/walk_e.png");
                SELECTED_IMAGES[3] = Image.createImage("/route/reset_e.png");
            } catch (IOException e) {
                error(e.getMessage());
            }

            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));

            Orientation.init(midlet);

            centeringComponent = new CenteringComponent(this,
                    getCommandListener(), null);
            map.addMapComponent(centeringComponent);

            if (hasPointerEvents()) {
                map.removeMapComponent(map.getMapComponent("DefaultCursor"));
            } else {
                note("Touch not enabled.", 5000);
            }
            map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

            // Ensure that the Zoom Buttons are at the back of the display
            // queue.
            moveZoomButtonToBack();

            try {
                SideBarComponent sb = new SideBarComponent(this,
                        getCommandListener());

                sb.setCommands(SELECTED_IMAGES, UNSELECTED_IMAGES, LABELS,
                        new Command[] {
                    CALCULATE, CALCULATE_WITH_MODE, CALCULATE_WITH_MODE, RESET});
                map.addMapComponent(sb);
            } catch (IOException e) {
                error(e.getMessage());
            }

            if (isGestureSupported()) {
                longTouch = new GestureLongTouch(this, getCommandListener(), ADD);
            } else {
                longTouch = new TimerLongTouch(this, getCommandListener(), ADD);
            }
            map.addMapComponent(longTouch);

        }

        /**
         * Adds center coordinate to waypoint parameters list and adds marker to
         * point out the location.
         * @param gc the coordinate to add.
         */
        private void addWaypoint(GeoCoordinate gc) {

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
                addWaypoint(longTouch.getTouchAt());
                if (hasSufficientWaypoints() && !hasPointerEvents()) {
                    addCommand(CALCULATE);
                    addCommand(CALCULATE_WITH_MODE);
                }
            } else if (c == CALCULATE) {
                if (hasSufficientWaypoints()) {
                    mode = new Mode();
                    makeRoutingRequest(mode);
                }
            } else if (c == CALCULATE_WITH_MODE) {
                if (hasSufficientWaypoints()) {
                    mode = new Mode();

                    mode.setTransportModes(new int[] { TransportMode.PEDESTRIAN});

                    makeRoutingRequest(mode);
                }
            } else if (c == RESET) {
                removeCommand(RESET);
                removeCommand(SHOW_MANUEVERS);
                addCommand(ADD);
                map.removeAllMapObjects();
                wpl = new WaypointParameterList();
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
         * Whether there are enough waypoints to make a route calculation.
         * @return <code>true</code> if there are enough waypoints to make a route calculation,
         * <code>false</code> otherwise.
         */
        private boolean hasSufficientWaypoints() {
            return wpl.getWaypointCoordinates().length > 1;
        }

        /**
         * Executes routing with the given modes.
         * @param modes the routing mode(s) to add.
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

                map.addMapObject(
                        mapFactory.createMapPolyline(routes[i].getShape(), 3));
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
}
