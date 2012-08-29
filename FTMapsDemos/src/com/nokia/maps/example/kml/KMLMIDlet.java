package com.nokia.maps.example.kml;


import com.nokia.maps.example.Base;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


/**
 * KML loading Midlet.
 */
public class KMLMIDlet extends MIDlet {
    /**
     * This method is called when the application is started. It sets the application
     * context, initialises the display  and starts up the demo.
     */
    public void startApp() {

        // Please initialise the appId and token first
        Base.InitialiseAuth();

        Display display = Display.getDisplay(this);
        KMLDemo md = new KMLDemo(display, this);

        display.setCurrent(md);

    }

    /**
     *  Pause the app - nothing to do.
     */
    protected void pauseApp() {}

    /**
     *  Clean up goes here. Nothing to do.
     *
     * @param unconditional whether the Midlet has any choice in the matter.
     */
    protected void destroyApp(boolean unconditional) {}

}
