package com.nokia.maps.gesture;


import com.nokia.maps.component.touch.ExtendedEventListener;
import com.nokia.maps.component.touch.TouchComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;


/**
 * 
 * Full Touch Gesture support initialisation.
 *
 */
public class FullTouchGestureHandler extends GestureHandler implements
        GestureListener {

    /**
     * When a Canvas is registered, ensure we receive all available
     * extended events from it. There is no need to re-invent the simple
     * touch events, just handle the complex ones.
     */
    protected void setMapCanvas(MapCanvas mapCanvas) {
        super.setMapCanvas(mapCanvas);
        GestureRegistrationManager.setListener(mapCanvas, this);

        // Check to see if PINCH and FLICK are supported.
        int supportedGestures = 0;

        if (GestureInteractiveZone.isSupported(
                GestureInteractiveZone.GESTURE_PINCH)) {
            supportedGestures = supportedGestures
                    | GestureInteractiveZone.GESTURE_PINCH;
        }

        if (GestureInteractiveZone.isSupported(
                GestureInteractiveZone.GESTURE_FLICK)) {
            supportedGestures = supportedGestures
                    | GestureInteractiveZone.GESTURE_FLICK;
        }
        
        
        if (GestureInteractiveZone.isSupported(
                GestureInteractiveZone.GESTURE_LONG_PRESS)) {
            supportedGestures = supportedGestures
                    | GestureInteractiveZone.GESTURE_LONG_PRESS;
        }

        // Register the active Zone - i.e. the whole Canvas.
        GestureInteractiveZone gestureZone = new GestureInteractiveZone(
                supportedGestures);

        GestureRegistrationManager.register(mapCanvas, gestureZone);
    }

    /**
     * If a FLICK or a PINCH have occurred, check to see if any Components
     * wish to handle them.
     */
    public void gestureAction(Object arg0, GestureInteractiveZone aGestureZone,
            GestureEvent event) {

        MapComponent[] components = getMapCanvas().getMapDisplay().getAllMapComponents();
        int i = components.length - 1;

        while (i > 0) {
            i--;
            if (components[i] instanceof TouchComponent) {
                if (doGesture(event, ((TouchComponent) components[i]).getExtendedEventListener())) {
                    break;
                }
            }
        }

    }

    /**
     * Pass the details of an event to any registered  TouchComponent
     * @param event
     * @param listener
     * @return <code>true</code> if the Event has been consumed, <code>false</code> otherwise.
     */
    private boolean doGesture(GestureEvent event, ExtendedEventListener listener) {

        int eventType = event.getType();
        boolean consumed = false;

        switch (eventType) {

        case GestureInteractiveZone.GESTURE_FLICK:

            consumed = listener.flick(event.getStartX(),
                    event.getStartY(), event.getFlickDirection(),
                    event.getFlickSpeed(), event.getFlickSpeedX(),
                    event.getFlickSpeedY());

            break;

        case GestureInteractiveZone.GESTURE_PINCH:
            consumed = listener.pinch(event.getStartX(),
                    event.getStartY(), event.getPinchCenterX(),
                    event.getPinchCenterY(), event.getPinchCenterChangeX(),
                    event.getPinchCenterChangeY(),
                    event.getPinchDistanceStarting(),
                    event.getPinchDistanceCurrent(),
                    event.getPinchDistanceChange());
            break;
            
        case GestureInteractiveZone.GESTURE_LONG_PRESS:
            consumed = listener.longPress(event.getStartX(),
                    event.getStartY());
            break;


        }

        return consumed;
    }
    
    public boolean isGestureSupported(){
    	return true;
    }

}
