
package com.nokia.maps.example.kml;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;


/**
 * An adaptor Pattern for switching between HTML and {@link Item} visible
 * on a {@link Form}
 */
public class HTMLAdaptor extends org.xml.sax.helpers.DefaultHandler {

    private String text;
    private StringBuffer bufferText;
    private Vector items;

    /**
     * Parses the xhtml text of a name or description to strip out HTML elements.
     * @param xhtml
     */
    public void parse(String xhtml) {

        // this will handle our XML
        items = new Vector();
        bufferText = new StringBuffer();

        if (xhtml == null || "".equals(xhtml)) {
            return;
        }

        xhtml = "<div>" + xhtml + "</div>";

        try {
            InputStream in = new ByteArrayInputStream(xhtml.getBytes("UTF-8"));
            // get a parser object
            javax.xml.parsers.SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();

            // parse the XHTML data stream
            parser.parse(in, this);
        } catch (Exception e) {
            // Swallow the exception id HTML is poorly formatted.
            e.printStackTrace();
        }

    }

    /**
     *  The start of processing an XML Element . startElement is the opening part of the tag "<tagname...>"
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws org.xml.sax.SAXException
     */
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {

        if ("IMG".equalsIgnoreCase(qName)) {
            Image im = downloadImage(attributes.getValue("src"));
            String altText = attributes.getValue("alt");

            items.addElement(
                    new ImageItem("", im, ImageItem.LAYOUT_DEFAULT,
                    altText != null ? altText : "", Item.PLAIN));
        }
    }

    /**
     * keep a record of any characters we see along the way
     * @param ch
     * @param start
     * @param length
     * @throws org.xml.sax.SAXException
     */
    public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
        text = new String(ch, start, length).trim();
    }

    /**
     *  The end of processing an XML Element . endElement is the closing part of the tag "<tagname...>"
     *
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws org.xml.sax.SAXException
     */
    public void endElement(String uri, String localName, String qName) throws org.xml.sax.SAXException {

        if (text != null) {
            bufferText.append(text);
            items.addElement(new StringItem("", text, Item.PLAIN));

            text = null;
        }
    }

    /**
     * @return all of the HTML text and Images as an array of Items.
     */
    public Item[] getItems() {
        Item[] itemArray = new Item[items.size()];

        items.copyInto(itemArray);
        return itemArray;
    }

    /**
     *
     * @return the text only from the HTML.
     */
    public String getPlainText() {
        return bufferText.toString();
    }

    /**
     * Helper function to download a specified Image asset.
     * @param url - the URL of a file to download.
     */
    private Image downloadImage(String url) {

        Image im = null;
        ContentConnection c = null;
        DataInputStream dis = null;

        if (url != null) {

            try {
                try {
                    c = (ContentConnection) Connector.open(url);
                    int len = (int) c.getLength();

                    dis = c.openDataInputStream();
                    if (len > 0) {
                        byte[] data = new byte[len];

                        dis.readFully(data);
                        im = Image.createImage(data, 0, data.length);
                    }
                } catch (IOException ioe) {// Failed to read the url. Can't do anything about it, just don't
                    // update the image.
                } finally {
                    // Regardless of whether we are successful, we need to close
                    // Connections behind us. Basic Housekeeping.
                    if (dis != null) {
                        dis.close();
                    }
                    if (c != null) {
                        c.close();
                    }
                }
            } catch (IOException ioe) {// closure of connections may fail, nothing we can do about it.
            }
        }

        return im;
    }
}
