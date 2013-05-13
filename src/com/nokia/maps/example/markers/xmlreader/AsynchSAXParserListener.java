
package com.nokia.maps.example.markers.xmlreader;


/**
 *
 * Since XML parsing can take a log time, it makes sense to delegate it to another thread.
 * This interface defines two "well-known" callback methods, so processing can continue
 * once the XML has been parsed (or an error has occurred.)
 */
public interface AsynchSAXParserListener {

    /**
     * Called after a  parse request has finished.
     *
     */
    void onParseComplete();

    /**
     * Called when a parse request has failed.
     * @param error the reason for the failure.
     */
    void onParseError(Throwable error);
}
