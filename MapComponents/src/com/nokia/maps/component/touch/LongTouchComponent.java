package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.Point;

/**
 * The long touch component fires a Command if a Long Press has occurred,
 * and remembers the location of the position 
 * 
 */
public class LongTouchComponent extends TouchComponent {

	private static final String VERSION = "1.0";
	public static final String ID = "LongTouch";

	private final Displayable displayable;
	private final CommandListener commandListener;

	private Command longTouchCommand;
	private GeoCoordinate touchAt;

	/**
	 * Default constructor
	 */
	public LongTouchComponent() {
		super();
		displayable = null;
		commandListener = null;
		setCommand(null);
	}

	/**
	 * A constructor with a ready made command to fire when a long press has
	 * occurred.
	 * 
	 * @param mapCanvas
	 * @param commandListener
	 * @param command
	 */
	public LongTouchComponent(MapCanvas mapCanvas,
			CommandListener commandListener, Command command) {
		super();
		this.displayable = mapCanvas;
		this.commandListener = commandListener;
		this.setCommand(command);
	}

	public String getId() {
		return ID;
	}

	public String getVersion() {
		return VERSION;
	}

	public void mapUpdated(boolean zoomChanged) {// This component does not
													// respond to changes of map
													// state.
	}

	public void paint(Graphics g) {// There is no visual feedback associated
									// with this component.
	}

	/**
	 * Since the Long press reacts to the whole screen, the GUI is always
	 * touched.
	 */
	boolean isGUITouched(int x, int y) {
		return true;
	}

	/**
	 * On a touch event, fire the associated command (if we have one).
	 */
	protected void longPressAt(Point point) {

		touchAt = map.pixelToGeo(point);
		if (getCommand() != null && commandListener != null
				&& displayable != null) {
			commandListener.commandAction(getCommand(), displayable);
		}

	}

	public Command getCommand() {
		return longTouchCommand;
	}

	public void setCommand(Command focusCommand) {
		this.longTouchCommand = focusCommand;
	}

	/**
	 * 
	 * @return The location the last long press occurred at.
	 */
	public GeoCoordinate getTouchAt() {
		return touchAt;
	}

	/**
	 * The GUI is never active since this component does not react to Presses,
	 * only Long Presses.
	 */
	boolean isGUIActive(int x, int y) {
		return false;
	}

	protected void touchAt(Point point) {
		// This component doesn't do anything when touched.
	}

	protected boolean onLongPressEvent(int x, int y) {
		longPressAt(new Point(x, y));
		return TouchEventHandler.EVENT_CONSUMED;
	}

}
