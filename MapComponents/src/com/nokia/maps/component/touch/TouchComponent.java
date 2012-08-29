/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.touch;

import com.nokia.maps.component.AbstractEventListener;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;

/**
 * An abstract base class to handle touch events.
 */
public abstract class TouchComponent implements MapComponent {

	private final EventListener touchEventHandler;

	protected MapDisplay map;

	public TouchComponent() {
		touchEventHandler = new TouchEventHandler();
	}

	public EventListener getEventListener() {
		return touchEventHandler;
	}

	protected abstract boolean isGUITouched(int x, int y);
	protected abstract boolean isGUIActive(int x, int y);
	protected abstract void touchAt( Point point);
	

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

	protected class TouchEventHandler extends AbstractEventListener {

		/**
		 * Called when a pointer pressed event occurs.
		 * 
		 * @param x
		 *            the x coordinate
		 * @param y
		 *            the y coordinate
		 * @return <code>true</code> if pointer event was consumed
		 */
		public boolean pointerReleased(int x, int y) {

			// If the point on screen is within the area defined by the
			// associated GUI Item, then it needs to be handled.
			return(isGUITouched(x, y) && isGUIActive(x, y)) ?
				 onTouchEventEnd(x, y):  isGUITouched(x, y);
		}

		public boolean pointerDragged(int x, int y) {
			return  onDragEvent(x, y);
		}

		public boolean pointerPressed(int x, int y) {
			return (isGUITouched(x, y)) ?
					onTouchEventStart(x, y) : onTouchEventClear(x,y);
		}

	};

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

	public boolean onTouchEventStart(int x, int y) {
		// The default here is to do nothing.
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}
	
	
	public boolean onTouchEventClear(int x, int y) {
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}
	
	
	public boolean onDragEvent(int x, int y) {
		// The default here is to do nothing except consume the event
		return  isGUITouched(x, y);
	}
	
	

	
	
	

}
