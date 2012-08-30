package com.nokia.maps.component;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapMarker;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;

public class MapFocus {
	
	private static  MapFocus mapFocus;

	private MapFocus(){
		// I'm a singleton.
	}
	
	public static  MapFocus getInstance(){
		if (mapFocus == null){
			mapFocus = new MapFocus();
		}
		return mapFocus;
	}
	
	/**
	 * 
	 * @param currentMap - the map to interrogate.
	 * @return the MapObject at the center of the map.
	 */
	public   MapObject objectAtMapCenter( MapDisplay map) {
		return  map != null ? map.getObjectAt(new Point(map.getWidth() / 2,
				map.getHeight() / 2)): null;
	}
	
	public  Point mapObjectToPixel( MapDisplay map,  MapObject mapObject) {
		return map.geoToPixel(mapObjectToGeo(map, mapObject));
	}
	
	
	public  GeoCoordinate mapObjectToGeo( MapDisplay map, MapObject mo) {

		if (mo instanceof MapStandardMarker) {
			return ((MapStandardMarker) mo).getCoordinate();
		} else if (mo instanceof MapMarker) {
			return ((MapMarker) mo).getCoordinate();
		}
		return mo.getBoundingBox().getCenter();
	}

}