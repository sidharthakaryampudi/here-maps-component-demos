package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.Point;

/**
 * The timer long touch component is a backup for when the Gesture API is unavailable.
 * 
 */
public class TimerLongTouchComponent extends LongTouchComponent {

	private static final long THRESHOLD = 500;
	public static final String ID = "TimerLongTouch";

	private long time;

	/**
	 * Default constructor
	 */
	public TimerLongTouchComponent() {
		super();
	}
	
	
	public String getId() {
		return ID;
	}

	/**
	 * A constructor with a ready made command to fire when a long press has
	 * occurred.
	 * 
	 * @param mapCanvas
	 * @param commandListener
	 * @param command
	 */
	public TimerLongTouchComponent(MapCanvas mapCanvas,
			CommandListener commandListener, Command command) {
		super(mapCanvas, commandListener, command);

	}

	/**
	 * The GUI is only active if the threshold has been exceeded.
	 */
	boolean isGUIActive(int x, int y) {
		return System.currentTimeMillis() - time > THRESHOLD;
	}

	/**
	 * On a touch event, fire the associated command (if we have one).
	 */
	protected void touchAt(Point point) {
		longPressAt(point);
	}

	/**
	 * Start a time to see if the event will be handled.
	 */
	protected boolean onTouchEventStart(int x, int y) {
		time = System.currentTimeMillis();
		return super.onTouchEventStart(x, y);
	}

	/**
	 * Reset the time. Note that the event is not consumed.
	 */
	protected boolean onDragEvent(int x, int y) {
		time = System.currentTimeMillis();
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

	protected boolean onLongPressEvent(int x, int y) {
		return TouchEventHandler.EVENT_NOT_CONSUMED;
	}

}
