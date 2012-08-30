package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.nokia.maps.component.Credentials;

/**
 * This MIDlet just sets the Long Press demo as current Displayable.
 */
public class LongTouchMidlet extends MIDlet {

	/**
	 * Clean up goes here. Nothing to do.
	 * 
	 * @param unconditional
	 *            whether the Midlet has any choice in the matter.
	 */
	protected void destroyApp(boolean unconditional)
			throws MIDletStateChangeException {
		// TODO Auto-generated method stub
	}

	/**
	 * Pause the app - nothing to do.
	 */
	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	/**
	 * This method is called when the application is started. It sets the
	 * application context, initialises the display and starts up the demo.
	 */
	protected void startApp() throws MIDletStateChangeException {
		Credentials.InitialiseAuth();
		Display display = Display.getDisplay(this);
		LongTouchDemo md = new LongTouchDemo(display, this);

		display.setCurrent(md);

	}

}