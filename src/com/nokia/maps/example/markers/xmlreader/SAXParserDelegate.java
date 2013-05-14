/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.markers.xmlreader;


/**
 * The SAX Parser will fire events when an XML element or its contents are read.
 * In order to delegate the processing to a specific class to transfrom the data,
 * the events are defined in a "well-known" interface here.
 */
public interface SAXParserDelegate {

    /**
     * on Start is called when the XML element is read.
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     */
    void onStartElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes);

    /**
     * onEnd is called when the XML element is completed.
     * @param uri
     * @param localName
     * @param qName
     */
    void onEndElement(String uri, String localName, String qName);

    /**
     * onCharacters is called when the text between two XML elements is read.
     * @param ch
     * @param start
     * @param length
     */
    void onCharacters(char[] ch, int start, int length);
}
