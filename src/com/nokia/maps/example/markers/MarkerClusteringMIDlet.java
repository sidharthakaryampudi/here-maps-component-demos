package com.nokia.maps.example.markers;


import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.feedback.TooltipComponent;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.example.markers.xmlreader.XMLToMapMarkersAndClusters;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapDisplayState;


/**
 * Marker Clustering example
 */
public class MarkerClusteringMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {
        return new MarkerClusterExample(display, this);
    }

    protected String getTitle() {
        return "Marker Clustering";
    }

    protected String getDescription() {
        return "This demonstrates how to cluster markers - over one hundred markers and"
                + " associated textual data are added to a map through"
                + " invoking a series of asychronous XML requests. Marker clustering"
                + " occurs server-side to avoid unnecessary delays on the device";
    }

    private class MarkerClusterExample extends MapCanvasExample implements FocalEventListener {

        /**
         * This URL holds the location and description of over 100 table tennis tables in Berlin.
         */
        private static final String XML_CLUSTER_SERVER = "http://api.maps.nokia.com/downloads/java-me/cluster/";

        // These components display the data loaded.
        private final FocalObserverComponent focalObserver;
        private final TooltipComponent tooltip;
        private final MarkerClusterer clusterer;
        private final XMLToMapMarkersAndClusters xmlProcessor;

        public MarkerClusterExample(Display display, MIDlet midlet) {
            super(display, midlet, MapDisplay.MAP_RESOLUTION_128_x_128);

            tooltip = new TooltipComponent(this);
            map.addMapComponent(tooltip);

            focalObserver = new FocalObserverComponent(this);
            map.addMapComponent(focalObserver);

            xmlProcessor = new XMLToMapMarkersAndClusters(getMapFactory(),
                    focalObserver);
            clusterer = new MarkerClusterer(this, XML_CLUSTER_SERVER,
                    xmlProcessor);

            map.addMapComponent(clusterer);
            clusterer.mapUpdated(true);

            // Set up the map, this will initially display a map of Germany.
            map.setState(
                    new MapDisplayState(
                            new GeoCoordinate(52.500556, 13.398889, 0), 7));

        }

        /**
         * Whenever the complete map has been rendered on screen, check to see
         * if the markers need to be re-clustered.
         */
        public void onMapContentComplete() {
            clusterer.cluster();
        }

        /**
         * Add/remove a tooltip holding the textual description of a marker.
         * @param focus
         */
        public void onFocusChanged(Object focus) {
            if (focus != null) {
                tooltip.add((String) focus);
            } else {
                tooltip.clear();
            }
        }
    }
}
