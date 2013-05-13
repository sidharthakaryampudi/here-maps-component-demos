package com.nokia.maps.ui.helpers;


import com.nokia.mid.ui.orientation.Orientation;
import com.nokia.mid.ui.orientation.OrientationListener;


/**
 *
 * Initialization code for the Orientation API if present on the device.
 *
 *
 * The MapCanvasOrientation class relies on the presence of the Orientation and
 * OrientationListener classes. These are available from the Nokia 2.0 SDK
 * onward. If the class is not present, then the orientation degrades
 * gracefully.
 *
 * For older device, to ensure that the Orientation class is available at Compile time,
 * an additional stubs jar has been added to the build path. If Orientation is not needed
 * then the MapCanvasOrientation class and the associated stubs.jar can be removed.
 *
 */
public class MapCanvasOrientation extends com.nokia.maps.ui.helpers.Orientation implements
        OrientationListener {

    private static Object drawLock = new Object();

    /**
     * Constructor, sets up the orientation listener.
     */
    protected MapCanvasOrientation() {
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
                Orientation.setAppOrientation(Orientation.ORIENTATION_LANDSCAPE);
            }

            break;
        }
    }

}
