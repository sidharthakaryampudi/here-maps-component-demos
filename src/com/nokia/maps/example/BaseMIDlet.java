package com.nokia.maps.example;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.nokia.maps.map.MapCanvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;


/**
 * 
 * Framework for all the example midlets.
 *
 */
public abstract class BaseMIDlet extends MIDlet {

    protected BaseMIDlet() {
        super();
    }

    protected abstract MapCanvas getDemo(Display display);

    protected abstract String getTitle();

    protected abstract String getDescription();

    /**
     * This method is called when the application is started and sets MapCanvas
     * as demo as current Displayable.
     *
     * @see MIDlet
     */
    protected void startApp() throws MIDletStateChangeException {

        // Please initialise the appId and token first
        Base.InitialiseAuth();
        if (Base.checkAuth(this)) {

            Display display = Display.getDisplay(this);
            MapCanvas demo = getDemo(display);
            // Display an alert describing the content of the demo.
            Alert demoDescription = new Alert(getTitle(), getDescription(), null,
                    AlertType.INFO);
            Command OK = new Command("OK", Command.OK, 1);

            demoDescription.addCommand(OK);
            demoDescription.setCommandListener(
                    new ViewDemoListener(demo, display));
            demoDescription.setTimeout(Alert.FOREVER);

            startDemo(display, demo);
            display.setCurrent(demoDescription);

        }
    }
    
    /**
     * Most demos start with the map displayed.
     * @param display
     * @param demo
     */
    protected void startDemo(Display display, MapCanvas demo) {
        display.setCurrent(demo);
    }

    /**
     * Clean up goes here. Nothing to do.
     *
     * @param unconditional
     *            whether the Midlet has any choice in the matter.
     */
    protected void destroyApp(boolean unconditional)
        throws MIDletStateChangeException {}

    /**
     * Pause the app - nothing to do.
     */
    protected void pauseApp() {}

    /**
     * Just used to pause the demo prior to display.
     */
    private class ViewDemoListener implements CommandListener {

        private final MapCanvas demo;
        private final Display display;

        public ViewDemoListener(MapCanvas demo, Display display) {
            this.demo = demo;
            this.display = display;
        }

        /**
         * If any key is pressed, continue with the demo.
         * @param c
         * @param d
         */
        public void commandAction(Command c, Displayable d) {
            startDemo(display, demo);
        }
    }
}
