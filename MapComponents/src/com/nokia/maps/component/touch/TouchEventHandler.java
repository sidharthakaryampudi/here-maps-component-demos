package com.nokia.maps.component.touch;


import com.nokia.maps.component.AbstractEventListener;


public class TouchEventHandler extends AbstractEventListener implements ExtendedEventListener{

    private final TouchComponent component;

    public TouchEventHandler(TouchComponent component) {
        this.component = component;
    }

    /**
     * Called when a pointer released event occurs.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed,
     *         <code>false</code> otherwise.
     */

    public boolean pointerReleased(int x, int y) {

        // If the point on screen is within the area defined by the
        // associated GUI Item, then it needs to be handled.
        return (component.isGUITouched(x, y) && component.isGUIActive(x, y))
                ? component.onTouchEventEnd(x, y)
                : component.isGUITouched(x, y);
    }

    /**
     * Called when a pointer pressed event occurs.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed,
     *         <code>false</code> otherwise.
     */
    public boolean pointerDragged(int x, int y) {
        return component.onDragEvent(x, y);
    }

    /**
     * Called when a pointer pressed event occurs.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed
     */
    public boolean pointerPressed(int x, int y) {
        return (component.isGUITouched(x, y))
                ? component.onTouchEventStart(x, y)
                : component.onTouchEventClear(x, y);
    }
    
    
    /**
	 * Called from the GestureListener when a Flick has occurred.
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @param speed
	 * @param speedX
	 * @param speedY
	 * @return
	 */
	public boolean flick(int x, int y, float direction, int speed, int speedX,
			int speedY) {
		return component.isGUITouched(x, y) ? component.onFlickEvent(x, y,
				direction, speed, speedX, speedY)
				: TouchEventHandler.EVENT_NOT_CONSUMED;
	}

	/**
	 * Called from the Gesture Listener when a Pinch has occurred.
	 * 
	 * @param x
	 * @param y
	 * @param centerX
	 * @param centerY
	 * @param centerChangeX
	 * @param centerChangeY
	 * @param distanceStarting
	 * @param distanceCurrent
	 * @param distanceChange
	 * @return
	 */
	public boolean pinch(int x, int y, int centerX, int centerY,
			int centerChangeX, int centerChangeY, int distanceStarting,
			int distanceCurrent, int distanceChange) {
		return component.isGUITouched(x, y) ? component.onPinchEvent(x, y,
				centerX, centerY, centerChangeX, centerChangeY,
				distanceStarting, distanceCurrent, distanceChange)
				: TouchEventHandler.EVENT_NOT_CONSUMED;
	}
	
	
	/**
	 * Called from the GestureListener when a LongPress has occurred.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean longPress(int x, int y){
		return component.isGUITouched(x, y) ? component.onLongPressEvent(x, y)
				: TouchEventHandler.EVENT_NOT_CONSUMED;
	}


}


;
