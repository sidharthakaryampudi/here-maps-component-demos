package com.nokia.maps.component.touch;

import com.nokia.maps.map.EventListener;

public interface ExtendedEventListener extends  EventListener {
	
	/**
	 * Called from the GestureListener when a LongPress has occurred.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean longPress(int x, int y);
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
			int speedY) ;

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
			int distanceCurrent, int distanceChange);

}