package com.nokia.maps.component.feedback;

import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;

import com.nokia.maps.component.MapFocus;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapObject;

public class FocalObserverComponent implements MapComponent {

	private MapDisplay map;
	private static final String ID = "FocalObserver";
	private static final String VERSION = "1.0";
	private final Hashtable observedObjects;
	private final FocalEventListener listener;
	private Object data;

	/**
	 * Default Constructor.
	 */
	public FocalObserverComponent(FocalEventListener listener) {
		observedObjects = new Hashtable();
		this.listener = listener;
	}

	/**
	 * Associates a data object to a map object.
	 * 
	 * @param mo
	 * @param tooltip
	 */
	public void addData(MapObject mo, Object data) {
		if (mo != null) {
			observedObjects.put(mo, data);
		}
	}

	/**
	 * Associates a data object to the map object found at the center of the
	 * screen.
	 * 
	 * @param tooltip
	 */
	public void addData(Object data) {
		if (map != null) {
			addData(MapFocus.getInstance().objectAtMapCenter(map), data);
		}
	}

	/**
	 * Removes the association between a data object and a MapObject.
	 * 
	 * @param mo
	 */
	public void removeData(MapObject mo) {
		observedObjects.remove(mo);
	}

	/**
	 * Clears all data associations.
	 */
	public void clear() {
		observedObjects.clear();
	}

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
	 * When the map is updated, we need to check if there is data associated with this
	 * location, and if necessary, fire an event.
	 * 
	 * @param zoomChanged
	 */

	public void mapUpdated(boolean zoomChanged) {

		MapObject mo = MapFocus.getInstance().objectAtMapCenter(map);
		if (mo != null){
			map.removeMapObject(mo);
            map.addMapObject(mo);
		}
		Object newData = (mo != null )? observedObjects.get(mo): null;
		
		if ((newData == null && data != null)
                || (newData != null
                        && (newData.equals(data) == false))) {           
                listener.onFocusChanged(newData);           
        }
        data = newData;

	}

	public void paint(Graphics g) {
		// There is no visual feedback associated with this component.
	}

}
