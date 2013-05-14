/**
* Copyright (c) 2013 Nokia Corporation.
*/

package com.nokia.maps.example.markers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.AbstractMapComponent;
import com.nokia.maps.example.markers.xmlreader.AsynchSAXParserListener;
import com.nokia.maps.example.markers.xmlreader.DelegatingAsynchSAXParser;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapListener;
import com.nokia.maps.map.MapMarker;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;


/**
 * This map component will make an XML request to a server whenever the map has
 * been zoomed or moved by a significant distance.
 */
public class MarkerClusterer extends AbstractMapComponent implements
        AsynchSAXParserListener {

    private static final String ID = "MarkerClusterer";
    private static final String VERSION = "1.0";
    private final DelegatingAsynchSAXParser parser;
    private final ClusterMarkerDelegate delegate;
    private final Vector clustersInViewport;
    private final MapListener mapCanvas;
    private final String server;
    private boolean readXMLthenRecluster;
    private double thresholdDistance;
    private GeoCoordinate coord;

    /**
     *
     * @param mapCanvas the canvas to refresh once reclustering has occurred.
     * @param focalObserver a map component to load with data associated with each marker.
     * @param server the base URL of the server which does the actual marker clustering
     * server-side.
     */
    public MarkerClusterer(MapCanvas mapCanvas, String server, ClusterMarkerDelegate delegate) {
        super(ID, VERSION);
        parser = new DelegatingAsynchSAXParser();
        this.delegate = delegate;

        clustersInViewport = new Vector();
        this.mapCanvas = mapCanvas;
        this.server = server;
    }

    /**
     * If the map has moved significantly, a request is made to re-cluster the map markers.
     * This is called only once the complete map is rendered, to avodi unnecessary re-clustering
     * whilst panning the map.
     */
    public void cluster() {
        if (readXMLthenRecluster) {

            readXMLthenRecluster = false;
            GeoBoundingBox viewPort = new GeoBoundingBox(
                    map.pixelToGeo(new Point(0, 0)),
                    map.pixelToGeo(new Point(map.getWidth(), map.getHeight())));

            try {
                // Make an asynchronous XML request, the response is consumed in
                // the  <code>onParseComplete()</code> method below.
                parseXMLFromServer(viewPort);
            } catch (IOException ioe) {
                // If the server fails to respond - display an error message.
                System.out.println(ioe.getMessage());
            }

        }

    }

    /**
     * This method is called whenever the component is attached to a
     * <code>MapDisplay</code>. Ensure that the current centre and the
     * distance required to  move before reclustering are set.
     *
     * @param map
     *            the <code>MapDisplay</code> the component is currently
     *            attached to.
     */
    public void attach(MapDisplay map) {
        super.attach(map);
        coord = map.getCenter();
        thresholdDistance = map.getCenter().distanceTo(
                map.pixelToGeo(
                        new Point(map.getWidth() / 4, map.getHeight() / 2)));
    }

    /**
     * This method is called when the attached {@link com.nokia.maps.map.MapDisplay MapDisplay} state changes.
     * Set a flag if either:
     * <ul><li>The zoom has changed</li>
     *  <li>The map has moved further than the threshold distance.</li>
     * </ul>
     * Setting the flag will mean the markers will be re-clustered once the complete map has been updated.
     * @param zoomChanged Whether the zoom level of the <code>MapDisplay</code> has been changed.
     *            <code>true</code> if the zoom level has changed
     */
    public void mapUpdated(boolean zoomChanged) {
        super.mapUpdated(zoomChanged);
        if (zoomChanged) {
            thresholdDistance = map.getCenter().distanceTo(
                    map.pixelToGeo(
                            new Point(map.getWidth() / 4, map.getHeight() / 2)));
        }
        if (zoomChanged || map.getCenter().distanceTo(coord) > thresholdDistance) {
            // The XML should only hold the items in the viewport.
            readXMLthenRecluster = true;
        }
    }

    /**
     * This function is called once XML Parsing is complete. Since this component has already
     * definined the SAXParserDelegate used, the result set is well defined - it must contain
     * markers and clusters. Just add the map objects within the current viewport to the map.
     */
    public void onParseComplete() {
        map.removeAllMapObjects();
        addObjectsInViewPort();
        mapCanvas.onMapContentUpdated();
    }

    /**
     * Should an error occur, the XML parser will throw it here.
     *
     * @param error
     */
    public void onParseError(Throwable error) {
        System.out.println(error);
    }

    /**
     * Having received some XML data, object within the current viewport are added to the map.
     */
    private void addObjectsInViewPort() {
        coord = map.getCenter();
        clustersInViewport.removeAllElements();
        MapObject[] mo = delegate.getContainer().getAllMapObjects();

        GeoBoundingBox viewPort = new GeoBoundingBox(
                map.pixelToGeo(new Point(0, 0)),
                map.pixelToGeo(new Point(map.getWidth(), map.getHeight())));

        for (int i = 0; i < mo.length; i++) {
            if (viewPort.contains(mapObjectToGeo(mo[i]))) {
                clustersInViewport.addElement(mo[i]);
                try {
                    map.addMapObject(mo[i]);
                } catch (IllegalArgumentException iae) {// Already added
                }

            }
        }

    }

    /**
     * This method returns the  <code>GeoCoordinate</code> of a given <code>MapObject</code>.
     * @param map the current map
     * @param mo the MapObject to use for the calculation.
     * @return The Geocoordinate at the focal point of a mapObject.
     */
    private GeoCoordinate mapObjectToGeo(MapObject mo) {

        if (mo instanceof MapStandardMarker) {
            return ((MapStandardMarker) mo).getCoordinate();
        } else if (mo instanceof MapMarker) {
            return ((MapMarker) mo).getCoordinate();
        }
        return mo.getBoundingBox().getCenter();
    }

    /**
     * This method makes an I/O request for retrieving a XML file via http.
     *
     */
    private void parseXMLFromServer(GeoBoundingBox viewPort) throws IOException {

        HttpConnection http = null;
        InputStream in = null;

        int zoom = map.getZoomLevel();

        zoom = (zoom < 7) ? 7 : (zoom > 16) ? 16 : zoom;
        delegate.getContainer().removeAllMapObjects();
        StringBuffer buf = new StringBuffer(server);

        buf.append(zoom);
        buf.append(".xml");

        buf.append("?lat1=");
        buf.append(viewPort.getTopLeft().getLatitude());
        buf.append("&lng1=");
        buf.append((viewPort.getTopLeft().getLongitude()));
        buf.append("&lat2=");
        buf.append(viewPort.getBottomRight().getLatitude());
        buf.append("&lng2=");
        buf.append((viewPort.getBottomRight().getLongitude()));

        try {

            http = (HttpConnection) Connector.open(buf.toString());
            http.setRequestMethod(HttpConnection.GET);
            if (http.getResponseCode() == HttpConnection.HTTP_OK) {

                int length = (int) http.getLength();

                in = http.openDataInputStream();
                if (length > 0) {
                    byte serverData[] = new byte[length];

                    in.read(serverData);
                    parser.parse(new ByteArrayInputStream(serverData), // Pass in data of a known format.
                            delegate, // Include a processing delegate which
                            // will handle this format.
                            this);

                }
            }
        } finally {
            // Clean up
            if (in != null) {
                in.close();
            }
            if (http != null) {
                http.close();
            }
        }

    }
}
