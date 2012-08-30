package com.nokia.maps.orientation;


import javax.microedition.midlet.MIDlet;


/**
 * 
 * Initialization code for the Orientation API,
 * orientation support is only enabled if present on the device.
 * 
 */
public class MapOrientator {

    private static final String APP_ORIENTATION = "Nokia-MIDlet-App-Orientation";
    private static MapOrientator orientator;
    private static final String MANUAL = "manual";

    protected MapOrientator() {// I'm a Singleton.
    }

    public static final void  init(MIDlet midlet) {
        if (orientator == null) {
			
            try {
                Class clazz = Class.forName(
                        "com.nokia.maps.orientation.FullTouchOrientator");
				
                orientator = isManualOrientation(midlet)
                        ? (MapOrientator) clazz.newInstance()
                        : new MapOrientator();
            } catch (NoClassDefFoundError e) {
                orientator = new MapOrientator();
            } catch (Exception e) {
                // Class.forName potentially throws some fatal error
                // messages we won't handle them here for clarity, but wrap them
                // instead.
                throw new RuntimeException(e.getMessage());
            }

        }
    }
	
    public static boolean isManualOrientation(MIDlet midlet) {
        return MANUAL.equalsIgnoreCase(midlet.getAppProperty(APP_ORIENTATION));
    }

}
