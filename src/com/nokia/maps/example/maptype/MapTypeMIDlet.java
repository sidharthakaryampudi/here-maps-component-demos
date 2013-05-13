package com.nokia.maps.example.maptype;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.MapProvider;
import com.nokia.maps.map.MapSchemeListener;


/**
 * Minimal MIDP application to show map content to the user.
 */
public class MapTypeMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return  new MapTypeExample(display, this);
    }

    protected String getTitle() {
        return "Switching Map Types";
    }

    protected String getDescription() {
        return "Change from street map to terrain, satellite or show"
                + "public  transport lines";
    }

    /**
     * Switching between map types.
     *
     * This example shows how to switch map type programmatically.
     */
    public class MapTypeExample extends MapCanvasExample implements MapSchemeListener {

        private MapProvider[] mapSchemes;
        private Command[] options;

        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        /**
         * Example for switching map types.
         * @param display the screen display
         * @param midlet the midlet.
         */
        public MapTypeExample(Display display, MIDlet midlet) {
            super(display, midlet);
            init();
        }

        private void init() {
            // request all available base map types using an asynchronous request.
            map.getAvailableBaseMaps(this);
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));
        }

        /**
         * Called when the list of available map schemes has been downloaded from
         * the network. This indicates that online maps are available and that the
         * list of schemes returned from {@link MapDisplay#getAvailableMapOverlays() } is
         * complete.
         */
        public void onMapSchemesAvailable(MapProvider[] mapSchemes) {
            this.mapSchemes = mapSchemes;
            setTitle(map.getBaseMap().getName());
            options = new Command[mapSchemes.length];

            for (int i = 0; i < mapSchemes.length; i++) {
                options[i] = new Command(mapSchemes[i].getName(), Command.HELP,
                        5);
                addCommand(options[i]);
            }
            display.setCurrent(this);
        }

        /**
         * Called when there is an error in the downloading of map schemes.
         *
         */
        public void onMapSchemesError(Throwable reason) {
            error(reason.toString());
        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(final Command c) {
            for (int i = 0; i < options.length; i++) {
                if (c == options[i]) {
                    try {
                        map.setBaseMapType(mapSchemes[i]);
                        setTitle(map.getBaseMap().getName());
                    } catch (IllegalArgumentException iae) {
                        error(iae.toString());
                    }
                    break;
                }
            }
        }
    }
}
