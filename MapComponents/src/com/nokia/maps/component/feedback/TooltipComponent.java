package com.nokia.maps.component.feedback;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import com.nokia.maps.component.MapFocus;
import com.nokia.maps.gui.TooltipRenderer;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapObject;

/**
 * A Custom MapComponent which displays a Tooltip.
 */
public class TooltipComponent implements MapComponent {

	private MapDisplay map;
	public static final String ID = "Tooltip";
	private static final String VERSION = "1.0";
	private final Hashtable tooltipTexts;
	private final TooltipRenderer renderer;

	/**
	 * Default Constructor.
	 */
	public TooltipComponent() {
		tooltipTexts = new Hashtable();
		renderer = new TooltipRenderer();
	}

	/**
	 * Adds a tooltip text to a map object.
	 * 
	 * @param mo
	 * @param tooltip
	 */
	public void add(MapObject mo, String tooltip) {
		if (mo != null) {
			tooltipTexts.put(mo, tooltip);
		}
	}

	/**
	 * Adds a tooltip text to the map object found at the center of the screen.
	 * 
	 * @param tooltip
	 */
	public void add(String tooltip) {
		if (map != null) {
			add(MapFocus.getInstance().objectAtMapCenter(map), tooltip);
		}
	}


	/**
	 * Removes a tooltip text from a MapObject.
	 * 
	 * @param mo
	 */
	public void remove(MapObject mo) {
		tooltipTexts.remove(mo);
	}
	
	/**
	 * Clears all tooltips.
	 */
	public void clear() {
		tooltipTexts.clear();
	}

	/**
	 * Attaches a Map to the Map Component.
	 * 
	 * @param map
	 */
	public void attach(MapDisplay map) {
		this.map = map;
		renderer.setPreferredDimensions(map.getWidth(), map.getHeight());
	}

	// from MapComponent
	public void detach(MapDisplay map) {
		this.map = null;
	}

	

	// from MapComponent
	public String getId() {
		return ID;
	}

	// from MapComponent
	public String getVersion() {
		return VERSION;
	}

	// from MapComponent
	public EventListener getEventListener() {
		return null; // This Map Component does not listen to events.
	}

	/**
	 * When the map is updated, we need to check if one of the MapObjects held
	 * in the Hashtable has the current focus. If so, the tooltip is set, the
	 * tooltip is cleared otherwise.
	 * 
	 * @param zoomChanged
	 */

	public void mapUpdated(boolean zoomChanged) {

		MapObject focusMO = MapFocus.getInstance().objectAtMapCenter(map);

		if (focusMO != null && tooltipTexts.containsKey(focusMO)) {
			renderer.setTooltip(MapFocus.getInstance().mapObjectToPixel(map, focusMO),
					(String) tooltipTexts.get(focusMO));
		} else {
			renderer.clearTooltip();
		}

	}



	/**
	 * Delegates the actual painting of the tooltip to an appropriate class.
	 * 
	 * @param g
	 */
	public void paint(Graphics g) {
		renderer.paint(g);

	}

}
