package com.nokia.maps.example.place;


import com.nokia.maps.example.Base;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


/**
 * This MIDlet just sets demo as current Displayable.
 */
public class PlaceMIDlet extends MIDlet {

    /**
     *  Clean up goes here. Nothing to do.
     *
     * @param unconditional whether the Midlet has any choice in the matter.
     */
    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {}

    /**
     *  Pause the app - nothing to do.
     */
    protected void pauseApp() {}

    /**
     * This method is called when the application is started. It sets the application
     * context, initialises the display  and starts up the demo.
     */
    protected void startApp() throws MIDletStateChangeException {

        // Please initialise the appId and token first
        Base.InitialiseAuth();

        Display display = Display.getDisplay(this);
        PlaceDemo md = new PlaceDemo(display, this);

        display.setCurrent(md);
    }
}
