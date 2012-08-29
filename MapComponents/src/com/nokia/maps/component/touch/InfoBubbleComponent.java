package com.nokia.maps.component.touch;

import java.util.Hashtable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import com.nokia.maps.component.MapFocus;
import com.nokia.maps.gui.InfoBubbleRenderer;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.Point;

/**
 * A Custom MapComponent which displays a context menu.
 */
public class InfoBubbleComponent extends GUITouchComponent {

	public static final String ID = "Infobubble";
	private static final String VERSION = "1.0";
	private final Hashtable bubbleTexts;
	// private final TooltipRenderer renderer;
	private final Hashtable commands;

	private final MapCanvas mapCanvas;
	private final CommandListener commandListener;
	private Command currentCommand;
	private MapObject currentObject;

	/**
	 * Default Constructor.
	 */
	public InfoBubbleComponent(MapCanvas mapCanvas, CommandListener listener) {
		super(new InfoBubbleRenderer());
		bubbleTexts = new Hashtable();
		commands = new Hashtable();
		commandListener = listener;
		this.mapCanvas = mapCanvas;
	}

	public void clear() {
		commands.clear();
		bubbleTexts.clear();
	}

	
	public void addData(MapObject mo, String text, Command command) {
		if (mo != null) {
			bubbleTexts.put(mo, text);
			commands.put(mo, command);
		}
	}

	
	public void addData(String text, Command command) {
		addData(MapFocus.getInstance().objectAtMapCenter(map), text, command);
	}

	/**
	 * Removes the association between a Context Menu and a MapObject.
	 * 
	 * @param mo
	 */
	public void removeData(MapObject mo) {
		bubbleTexts.remove(mo);
	}

	/**
	 * Attaches a Map to the Map Component.
	 * 
	 * @param map
	 */
	public void attach(MapDisplay map) {
		super.attach(map);
		getInfobubbleGUI().setPreferredDimensions(map.getWidth(),
				map.getHeight());
	}

	private InfoBubbleRenderer getInfobubbleGUI() {
		return ((InfoBubbleRenderer) getRenderer());
	}

	// from MapComponent
	public String getId() {
		return ID;

	}

	// from MapComponent
	public String getVersion() {
		return VERSION;
	}

	// overrides TouchComponent
	protected void touchAt(Point point) {
		mapCanvas.onMapContentUpdated();
		if ( currentCommand != null) {
			commandListener.commandAction(currentCommand,
					mapCanvas);
		}
	}
	
	public boolean onTouchEventStart(int x, int y) {
		getInfobubbleGUI().touchAt(x,y);
		return super.onTouchEventStart( x, y);
	}
	
	public boolean onDragEvent(int x, int y) {
		// Drag the menu items if visible.
		if (isGUIVisible()) {			
			getInfobubbleGUI().draggedTo(x,y);
		}
		return super.onDragEvent( x, y);
	}
	
	public boolean onTouchEventClear(int x, int y) {
		hideInfoBubble();
		return super.onTouchEventClear(x, y);
	}

	private void hideInfoBubble() {
		getInfobubbleGUI().clearTooltip();
		if (currentObject != null){
			currentObject.setVisible(true);
			currentObject = null;
		}
	}

	


	/**
	 * When the map is updated, we need to check if one of the MapObjects held
	 * in the Hashtable has the current focus. If so, the Context Menu is
	 * displayed, the Context Menu is cleared otherwise.
	 * 
	 * @param zoomChanged
	 */

	public void mapUpdated(boolean zoomChanged) {

		MapObject focusMO = MapFocus.getInstance().objectAtMapCenter(map);

		if (focusMO != null && bubbleTexts.containsKey(focusMO)) {
			showInfoBubble(focusMO);
		} else {
			hideInfoBubble();
		}

	}

	private void showInfoBubble(MapObject focusMO) {
		if (currentObject != focusMO && currentObject != null){
			currentObject.setVisible(true);
		}
		currentObject = focusMO;
		currentObject.setVisible(false);
		getInfobubbleGUI().setTooltip(MapFocus.getInstance().mapObjectToPixel(map, focusMO),
				(String) bubbleTexts.get(focusMO));
		currentCommand = ((Command) commands.get(focusMO));
	}
	
	
	
}
