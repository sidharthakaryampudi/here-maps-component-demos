
package com.nokia.maps.example.markers.xmlreader;


import java.io.InputStream;
import org.xml.sax.SAXException;


/**
 * This is an example of a very simple SAX Parser. Whenever an XML element is
 * found, it passes it on for the delegate to process it.
 */
public class DelegatingAsynchSAXParser extends org.xml.sax.helpers.DefaultHandler implements Runnable {

    private SAXParserDelegate delegate;
    private AsynchSAXParserListener listener;
    private InputStream in;

    /**
     * Method
     * @param in the XML data to process
     * @param delegate The actual class which will process the data received.
     * @param listener a callback function once the processing has been completed
     */public void parse(InputStream in, SAXParserDelegate delegate, AsynchSAXParserListener listener) {
        this.listener = listener;
        this.in = in;
        this.delegate = delegate;
        new Thread(this).start();
    }

    // Parsing an arbitrary length file can take a long time. much better to
    // make this operation Asynchronous.
    public void run() {
        try {
            // get a SAX parser object
            javax.xml.parsers.SAXParser parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();

            // get an InputStream from somewhere (could be HttpConnection, for example)
            // parse the XML data stream
            parser.parse(this.in, this);
            listener.onParseComplete();
        } catch (Throwable t) {
            listener.onParseError(t);
        }
    }

    // SAX Parser has found the start of an XML element
    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXException {
        delegate.onStartElement(uri, localName, qName, attributes);
    }

    // SAX Parser has found the end of an XML element
    public void endElement(String uri, String localName, String qName) throws SAXException {
        delegate.onEndElement(uri, localName, qName);
    }

    // SAX Parser has found characters between two XML elements
    public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
        delegate.onCharacters(ch, start, length);
    }
}
