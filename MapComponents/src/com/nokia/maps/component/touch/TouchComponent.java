/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.touch;

import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;

/**
 * An abstract base class to handle touch events and  actions from a GestureListener if one has been intialised.
 */
public abstract class TouchComponent implements MapComponent {

	private final EventListener touchEventHandler;

	protected MapDisplay map;

	/**
	 * Constructor - handler is initialized to handle standard Canvas touch events.
	 */
	public TouchComponent() {
		touchEventHandler = new TouchEventHandler(this);
	}

	/**
	 * The Event listener is not null - hence we are handling Touch.
	 */
	public EventListener getEventListener() {
		return touchEventHandler;
	}

	/**
	 * Whether the GUI has been pressed within the hit target area.
	 * @param x
	 * @param y
	 * @return
	 */
	protected abstract boolean isGUITouched(int x, int y);

	/**
	 * Whether the GUI item is active - i.e has not been cancelled.
	 * @param x
	 * @param y
	 * @return
	 */
	protected abstract boolean isGUIActive(int x, int y);

	/**
	 * 
	 * @param point
	 */
	protected abstract void touchAt(Point point);

	/**
	 * Attaches a Map to the Map Component.
	 * 
	 * @param map
	 */
	public void attach(MapDisplay map) {
		this.map = map;
	}

	// from MapComponent
	public void detach(MapDisplay map) {
		this.map = null;
	}

	protected Point getTouchPoint(int x, int y) {
		return new Point(x, y);
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
	public boolean onTouchEventEnd(int x, int y) {
		touchAt(getTouchPoint(x, y));
		return TouchEventHandler.EVENT_CONSUMED;
	}

	/**
	 * Called when a pointer press has started.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onTouchEventStart(int x, int y) {
		// This event is not handled by default.
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

	/**
	 * Called when the touch event has been cleared - usually by pressing out of the hit area.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onTouchEventClear(int x, int y) {
		// This event is not handled by default.
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

	/**
	 * Called when a drag event occurs.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean onDragEvent(int x, int y) {
		// The default here is to do nothing except consume the event
		return isGUITouched(x, y);
	}

	/**
	 * Called from the GestureListener when a Flick has occurred.
	 * @param x
	 * @param y
	 * @param direction
	 * @param speed
	 * @param speedX
	 * @param speedY
	 * @return
	 */
	public boolean onFlickEvent(int x, int y, float direction, int speed,
			int speedX, int speedY) {
		// This event is not handled by default.
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

	/**
	 * Called from the Gesture Listener when a Pinch has occurred.
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
	public boolean onPinchEvent(int x, int y, int centerX, int centerY,
			int centerChangeX, int centerChangeY, int distanceStarting,
			int distanceCurrent, int distanceChange) {
		// This event is not handled by default.
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

}
