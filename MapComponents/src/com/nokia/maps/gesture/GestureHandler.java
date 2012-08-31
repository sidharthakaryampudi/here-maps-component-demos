package com.nokia.maps.gesture;


import com.nokia.maps.map.MapCanvas;


/**
 * 
 * Initialization code for the Gesture API,
 * gesture support is only enabled if present on the device.
 * 
 */
public class GestureHandler {
    private MapCanvas mapCanvas;

    private static GestureHandler gesture;

    protected GestureHandler() {// I'm a Singleton.
    }

    public static final boolean init(MapCanvas canvas) {

        if (gesture == null) {

            try {
                Class clazz = Class.forName(
                        "com.nokia.maps.gesture.FullTouchGestureHandler");

                gesture = (GestureHandler) clazz.newInstance();
            } catch (NoClassDefFoundError e) {
                gesture = new GestureHandler();
            } catch (Exception e) {
                // Class.forName potentially throws some fatal error
                // messages we won't handle them here for clarity, but wrap them
                // instead.
                throw new RuntimeException(e.getMessage());
            }
            gesture.setMapCanvas(canvas);

        }
        
        return gesture.isGestureSupported();

    }

    protected MapCanvas getMapCanvas() {
        return mapCanvas;
    }

    protected void setMapCanvas(MapCanvas mapCanvas) {
        this.mapCanvas = mapCanvas;
    }
    
    public boolean isGestureSupported(){
    	return false;
    }

}
