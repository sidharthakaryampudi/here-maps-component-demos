package com.nokia.maps.example.component;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplayState;


/**
 *
 * This MIDlet adds and removes MapComponents from the MapDisplay.
 */
public class MapComponentMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new MapComponentExample(display, this);

    }

    protected String getTitle() {
        return "Map Components";
    }

    protected String getDescription() {
        return "Demonstrates the addition/removal of Map Components. The five"
                + " standard components and a pair of simple custom components are added to"
                + " the map and can be removed.";
    }

    /**
     * This demo adds and removes MapComponents from the MapDisplay. The five
     * standard components and a pair of simple custom components are added to
     * the map and can be removed.
     */
    private class MapComponentExample extends MapCanvasExample {

        private final Command[] ADD_REMOVE_COMPONENT;
        private final MapComponent[] MAP_COMPONENTS;

        public MapComponentExample(Display display, MIDlet midlet) {
            super(display, midlet);

            map.setState(new MapDisplayState(new GeoCoordinate(0.0, 0.0, 0), 1));

            //
            // The HelloWorldComponent writes Hello World on the map.
            //
            map.addMapComponent(new HelloWorldComponent());
            // The PixelToGeoComponent displays the Geocoodinate touched.
            //
            // Because the PixelToGeoComponent is added AFTER the
            // ZoomImgComponent,
            // it will respond to the touch event BEFORE it.
            map.addMapComponent(new PixelToGeoComponent(display));

            MAP_COMPONENTS = map.getAllMapComponents();
            ADD_REMOVE_COMPONENT = new Command[MAP_COMPONENTS.length];
            for (int i = 0; i < MAP_COMPONENTS.length; i++) {
                ADD_REMOVE_COMPONENT[i] = new Command(MAP_COMPONENTS[i].getId(),
                        Command.ITEM, 3);
                addCommand(ADD_REMOVE_COMPONENT[i]);
            }

        }

        /**
         * Standard Command button interaction. Map Components may be added or
         * removed from the MapDisplay.
         *
         * @param c
         *            - The Command corresponding to the component to Add/
         *            Remove
         *
         */
        public void commandRun(Command c) {
            for (int i = 0; i < ADD_REMOVE_COMPONENT.length; i++) {
                if (c == ADD_REMOVE_COMPONENT[i]) {
                    MapComponent component = map.getMapComponent(
                            ADD_REMOVE_COMPONENT[i].getLabel());

                    if (component != null) {
                        map.removeMapComponent(component);
                    } else {
                        map.addMapComponent(MAP_COMPONENTS[i]);
                    }
                    break;
                }
            }
            onMapContentUpdated();
        }
    }

}
