package com.nokia.maps.example.routing;


import com.nokia.maps.example.Base;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


/**
 * This MIDlet just sets example as current Displayable.
 */
public class RoutingMIDlet extends MIDlet {

    /**
     *  Clean up goes here. Nothing to do.
     *
     * @param unconditional whether the Midlet has any choice in the matter.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {}

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
        RoutingDemo d = new RoutingDemo(display, this);

        display.setCurrent(d);
    }
}
