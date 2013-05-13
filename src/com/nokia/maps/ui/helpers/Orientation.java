package com.nokia.maps.ui.helpers;


import javax.microedition.midlet.MIDlet;


/**
 *
 * Initialization code for the Orientation API, orientation support is only
 * enabled if present on the device.
 *
 * For older device, to ensure that the Orientation class is available at Compile time,
 * an additional stubs jar has been added to the build path. If Orientation is not needed
 * then the Orientation, MapCanvasOrientator  and the associated stubs.jar can be removed.
 */
public class Orientation {

    private static final String APP_ORIENTATION = "Nokia-MIDlet-App-Orientation";
    private static Orientation orientator;
    private static final String MANUAL = "manual";

    protected Orientation() {// I'm a Singleton.
    }

    /**
     * Initialisation of the Midlet when requiring landscape and portrait orientations.
     * The <code>MapCanvas</code> can be displayed either way up.
     * @param midlet The app to run
     */
    public static void init(MIDlet midlet) {
        if (orientator == null) {

            try {
                // The MapCanvasOrientator class relies on the presence
                // of the Orientation and OrientationListener classes.
                // These are available from the Nokia 2.0 SDK onward.
                // If the class is not present, then the
                // orientatiation degrades gracefully.
                //
                // To ensure that the named class is available at Compile time,
                // and additional stubs jar has been added to the build path.
                Class clazz = Class.forName(
                        "com.nokia.maps.ui.helpers.MapCanvasOrientation");

                orientator = isManualOrientation(midlet)
                        ? (Orientation) clazz.newInstance()
                        : new Orientation();
            } catch (NoClassDefFoundError e) {
                orientator = new Orientation();
            } catch (Exception e) {
                // Class.forName potentially throws some fatal error
                // messages we won't handle them here for clarity, but wrap them
                // instead.
                throw new RuntimeException(e.getMessage());
            }

        }
    }

    /**
     * Whether or no the current MIDlet app uses the Nokia-MIDlet-App-Orientation attribute
     * @param midlet  The app to run
     * @return <code>true</code> if the Nokia-MIDlet-App-Orientation attribute is manual,
     * 	<code>false</code> otherwise.
     */
    public static boolean isManualOrientation(MIDlet midlet) {
        return MANUAL.equalsIgnoreCase(midlet.getAppProperty(APP_ORIENTATION));
    }

}
