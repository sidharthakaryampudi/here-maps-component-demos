/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.markers.xmlreader;


import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.example.markers.ClusterMarkerDelegate;
import com.nokia.maps.map.MapContainer;
import com.nokia.maps.map.MapFactory;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;


/**
 * <p>This is an example of a very simple delegate of a SAX Parser which has been written to
 * read XML data of a known format. The data is processed so that the output can
 * be displayed as markers and clusters on a Nokia Map.</p>
 *
 *  <p>The syntax of the XML is as follows:</p>
 *  <code>
 *  &lt;marker id="1" lat="52.4907" lng="13.4726" text="Nice table in Treptower Park overlooking the Spree"  /&gt;
 *  </code>
 */
public class XMLToMapMarkersAndClusters implements SAXParserDelegate, ClusterMarkerDelegate {

    // This is the name of the XML element to process.
    private static final String MARKER = "marker";
    private static final String CLUSTER = "cluster";
    // These are the attributes of the marker XML element:
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String TEXT = "text";
    private static final String SIZE = "size";
    // The processed output is held here:
    private final MapContainer container;
    private final FocalObserverComponent focalObserver;
    // The definition of how to display markers/ clusters
    // is found here.
    private final ClusterTheme theme;

    /**
     * Constructor
     * @param factory for creating map markers
     * @param focalObserver for holding non-marker data.
     */
    public XMLToMapMarkersAndClusters(MapFactory factory, FocalObserverComponent focalObserver) {
        this.theme = new ClusterTheme(factory);
        this.focalObserver = focalObserver;
        container = factory.createMapContainer();
    }

    // on Start is called when the XML element is read. The Attributes can
    // be processed at this point.
    public void onStartElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) {

        // The XML Element must be of the form:
        // <marker id="1" lat="52.4907" lng="13.4726" text="Nice table in Treptower Park overlooking the Spree"  />
        if (MARKER.equals(qName)) {
            MapObject noisePoint = theme.getNoisePointPresentation(
                    new GeoCoordinate(
                            Double.parseDouble(attributes.getValue(LATITUDE)),
                            Double.parseDouble(attributes.getValue(LONGITUDE)),
                            0));

            container.addMapObject(noisePoint);
            // Now retrieve any non-geographic data required for the App
            focalObserver.addData(noisePoint, attributes.getValue(TEXT));
        } else if (CLUSTER.equals(qName)) {
            // The XML Element must be of the form:
            // <cluster lat="52.4907" lng="13.4726" size="102" />
            MapObject clusterPoint = theme.getClusterPresentation(
                    new GeoCoordinate(
                            Double.parseDouble(attributes.getValue(LATITUDE)),
                            Double.parseDouble(attributes.getValue(LONGITUDE)),
                            0),
                            Integer.parseInt(attributes.getValue(SIZE)));

            container.addMapObject(clusterPoint);

        }
    }

    // onEnd is called when the XML element is completed, since this is a simple
    // example, no processing is required.
    public void onEndElement(String uri, String localName, String qName) {// No need to do anything here
    }

    // onCharacters is called when the text between two XML elements is read.
    // The text here can be used for further processing, but is not  required
    // in this example.
    public void onCharacters(char[] ch, int start, int length) {// No text between the XML elements.
    }

    /**
     * @return the Map container with markers on each point specified by the XML
     */
    public MapContainer getContainer() {
        return container;
    }

    /**
     * The Cluster theme defines how display clusters and individual markers that
     * lie outside of a cluster.
     */
    private class ClusterTheme {
        private static final int RED = 0xFFB22222;

        private static final int ORANGE = 0xFFFF7F50;

        private static final int GREEN = 0xFFFFA500;

        public static final int BLUE = 0xFF1E90FF;

        private final MapFactory factory;
        private final int[] limit = { 10, 30, 50};
        private final int[] COLOR = { GREEN, ORANGE, RED};

        /**
         * Constructor
         * @param factory this is used to create the markers.
         */
        public ClusterTheme(MapFactory factory) {
            this.factory = factory;
        }

        /**
         * Returns a map object representing an unclustered marker.
         * @param coord the coordinate of a marker
         * @return a map object representing an unclustered marker
         */
        public MapObject getNoisePointPresentation(GeoCoordinate coord) {
            MapStandardMarker marker = factory.createStandardMarker(coord, 20,
                    "", MapStandardMarker.OCTAGON);

            marker.setColor(BLUE);
            return marker;
        }

        /**
         * Returns a map object representing a cluster
         * @param coord the coordinate of a cluster
         * @param size the number of markers within a cluster
         * @return a map object representing an cluster of marker
         */
        public MapObject getClusterPresentation(GeoCoordinate coord, int size) {
            MapStandardMarker marker = factory.createStandardMarker(coord,
                    size < 30 ? 30 : 35, String.valueOf(size),
                    MapStandardMarker.OCTAGON);

            marker.setColor(getColor(size));
            return marker;

        }

        /**
         * This method alters the cluster color based on the size.
         * @param size
         * @return the color for this cluster
         */
        private int getColor(int size) {
            int color = 0xFF32cd32;

            for (int i = 0; i < limit.length; i++) {
                if (size >= limit[i]) {
                    color = COLOR[i];
                }
            }
            return color;
        }
    }
}
