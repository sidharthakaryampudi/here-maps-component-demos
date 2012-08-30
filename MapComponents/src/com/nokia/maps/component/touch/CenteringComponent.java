package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.MapFocus;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapListener;
import com.nokia.maps.map.Point;

/**
 * The focus component checks to see if an object can be found underneath the
 * touch press and sets the center of the map accordingly
 * 
 */
public class CenteringComponent extends TouchComponent {

	private static final String VERSION = "1.0";
	public static final String ID = "Centerer";
	private final MapListener mapListener;
	private final Displayable displayable;
	private final CommandListener commandListener;
	private  Command focusCommand;

	/**
	 * Constructor which does not have any associated command
	 * @param mapListener
	 */
	public CenteringComponent(MapListener mapListener) {
		super();
		this.mapListener = mapListener;
		displayable = null;
		commandListener = null;
		setFocusCommand(null);
	}

	/**
	 * Constructor which will cause a command to fire if the object at
	 * the centre of the map is touched.
	 * @param mapCanvas
	 * @param commandListener
	 * @param focusCommand
	 */
	public CenteringComponent(MapCanvas mapCanvas,
			CommandListener commandListener, Command focusCommand) {
		super();
		this.mapListener = mapCanvas;
		this.displayable = mapCanvas;
		this.commandListener = commandListener;
		this.setFocusCommand(focusCommand);
	}

	public String getId() {
		return ID;
	}

	public String getVersion() {
		return VERSION;
	}

	public void mapUpdated(boolean zoomChanged) {
		// This component does not respond to changes of map state.
	}

	public void paint(Graphics g) {
		// There is no visual feedback associated with this component.
	}

	protected boolean isGUITouched(int x, int y) {
		return (map.getObjectAt(new Point(x, y)) != null);
	}
	
	protected boolean isGUIActive(int x, int y) {
		return true;
	}

	protected void touchAt(Point point){
		GeoCoordinate focus = MapFocus.getInstance().mapObjectToGeo(map, map.getObjectAt( point));

		if (focus.distanceTo(map.getCenter()) == 0d && commandListener != null) {
			fireFocusCommand();
		} else {
			map.setCenter(focus);
			mapListener.onMapContentUpdated();
		}

	}

	private void fireFocusCommand() {
		if (getFocusCommand() != null && commandListener != null && displayable != null ) {
			commandListener.commandAction(getFocusCommand(), displayable);
		}
	}

	
	public Command getFocusCommand() {
		return focusCommand;
	}

	public void setFocusCommand(Command focusCommand) {
		this.focusCommand = focusCommand;
	}

}
