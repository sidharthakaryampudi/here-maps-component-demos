package com.nokia.maps.orientation;

import com.nokia.mid.ui.orientation.Orientation;
import com.nokia.mid.ui.orientation.OrientationListener;

/**
 * 
 * Initialization code for the Orientation API if present on the device.
 * 
 */
public class FullTouchOrientator extends MapOrientator implements
		OrientationListener {

	private static Object drawLock = new Object();

	/**
	 * Constructor, sets up the orientation listener.
	 */
	protected FullTouchOrientator() {
		Orientation.addOrientationListener(this);
	}

	/**
	 * Standard handler for orienting the display.
	 */
	public void displayOrientationChanged(int newDisplayOrientation) {

		switch (newDisplayOrientation) {
		case Orientation.ORIENTATION_PORTRAIT:
		case Orientation.ORIENTATION_PORTRAIT_180:

			/** Change MIDlet UI orientation to portrait */
			synchronized (drawLock) {
				Orientation.setAppOrientation(Orientation.ORIENTATION_PORTRAIT);
			}
			break;

		case Orientation.ORIENTATION_LANDSCAPE:
		case Orientation.ORIENTATION_LANDSCAPE_180:

			/** Change MIDlet UI orientation to landscape */
			synchronized (drawLock) {
				Orientation
						.setAppOrientation(Orientation.ORIENTATION_LANDSCAPE);
			}

			break;
		}
	}

}
