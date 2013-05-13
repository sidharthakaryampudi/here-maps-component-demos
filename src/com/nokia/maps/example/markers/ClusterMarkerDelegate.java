package com.nokia.maps.example.markers;


import com.nokia.maps.example.markers.xmlreader.SAXParserDelegate;
import com.nokia.maps.map.MapContainer;


/**
 * Simple interface defining an object which holds map markers
 */
public interface ClusterMarkerDelegate extends SAXParserDelegate {

    /**
     * @return the Map container with markers on each point specified by the XML
     */
    public MapContainer getContainer();

}
