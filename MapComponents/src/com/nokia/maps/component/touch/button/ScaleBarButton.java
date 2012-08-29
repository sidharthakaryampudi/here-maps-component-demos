/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.touch.button;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.gui.GUIItemRenderer;
import com.nokia.maps.gui.TextButtonRenderer;
import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.map.Point;

/**
 * 
 * Scale bar component displays a scale in Kilometers or Miles
 */
public class ScaleBarButton extends TextButton {

	public static final String ID = "ScaleBar";
	private int y_coord;
	private int x_coord_start;
	private int x_coord_end;
	private boolean imperial;
	private double currentZoom = -1;
	private final int scaleBarColor;

	private final static int[] SCALE_IN_METRES = new int[] { 2000000, 1000000, 600000,
			300000, 150000, 75000, 30000, 20000, 10000, 5000, 2000, 1000, 500,
			250, 100, 50, 25, 12 };
	private final static String[] SCALE_IN_METRES_TEXT = new String[] { "2000 km",
			"1000 km", "600 km", "300 km", "150 km", "75 km", "30 km", "20 km",
			"10 km", "5 km", "2 km", "1 km", "500 m", "250 m", "100 m", "50 m",
			"25 m", "12m" };
	private final static int[] SCALE_IN_IMPERIAL = new int[] { 4828000, 2414000,
			1207000, 643737, 321868, 160934, 80467, 40233, 24140, 16093, 8046,
			3218, 1609, 457, 228, 91, 45, 22 };
	private final static String[] SCALE_IN_IMPERIAL_TEXT = new String[] { "3000 mi",
			"1500 mi", "750 mi", "400 mi", "200 mi", "100 mi", "50 mi",
			"25 mi.", "15 mi", "10 mi", "5 mi", "2 mi", "1 mi", "500 yds",
			"250 yds", "100 yds", "50 yds", "75 ft" };

	public ScaleBarButton() {
		this(Graphics.BOTTOM | Graphics.RIGHT);
	}

	public ScaleBarButton(int anchor) {
		this(anchor, TextButtonRenderer.WHITE, GUIItemRenderer.SMALL_FONT,
				new BackgroundBox(TextButtonRenderer.MID_GREY,
						TextButtonRenderer.CYAN, TextButtonRenderer.PALE_GREY,  TextButtonRenderer.CYAN)

		);
	}

	public ScaleBarButton(int anchor, int textColor, Font font,
			BackgroundBox background) {
		super(anchor, textColor, font, "", background);
		scaleBarColor = textColor;
	}

	// from MapComponent
	public String getId() {
		return ID;
	}

	/**
	 * When the map is updated, we need to check if one of the MapObjects held
	 * in the Hashtable has the current focus. If so, the tooltip is set, the
	 * tooltip is cleared otherwise.
	 * 
	 * @param zoomChanged
	 */
	public void mapUpdated(boolean zoomChanged) {

		double zoom = map.getZoomLevel();
		if (currentZoom != zoom) {
			setText(isImperial() ? getLegend(zoom, SCALE_IN_IMPERIAL_TEXT)
					: getLegend(zoom, SCALE_IN_METRES_TEXT));

		}

		positionGUIOnScreen();

		x_coord_start = getRenderer().getAnchor().getX()
				+ TextButtonRenderer.TEXT_MARGIN;
		y_coord = getRenderer().getAnchor().getY()
				+ getRenderer().getGUIData().getHeight() - 3;

	}

	private String getLegend(double zoom, String[] legends) {
		return (zoom <= legends.length - 1) ? legends[(int) zoom] : "";
	}

	private void calculateBar(double zoom, int[] barLengths) {
		if (zoom <= barLengths.length - 1) {

			GeoCoordinate start = map.pixelToGeo(new Point(x_coord_start,
					y_coord));
			x_coord_end = x_coord_start + 10;
			while (map.pixelToGeo(new Point(x_coord_end, y_coord)).distanceTo(
					start) < barLengths[(int) zoom]) {
				x_coord_end++;
				if (x_coord_end > getRenderer().getGUIData().getWidth()) {
					break;
				}
			}

		}
	}

	public void paint(Graphics g) {
		if ("".equals(getText()) == false) {
			super.paint(g);

			calculateBar(map.getZoomLevel(), isImperial() ? SCALE_IN_IMPERIAL
					: SCALE_IN_METRES);
			g.setColor(scaleBarColor);
			g.drawLine(x_coord_start, y_coord, x_coord_end, y_coord);
			g.drawLine(x_coord_start, y_coord + 1, x_coord_end, y_coord + 1);

			g.drawLine(x_coord_start, y_coord + 3, x_coord_start, y_coord);
			g.drawLine(x_coord_end, y_coord + 3, x_coord_end, y_coord);

		}
	}

	/**
	 * Toggles between metric and imperial measurements.
	 */
	protected void touchAt(Point point) {
		currentZoom = -1;
		setImperial(!isImperial());
		mapUpdated(false);
		super.touchAt(point);
	}

	/**
	 * @return the imperial
	 */
	public boolean isImperial() {
		return imperial;
	}

	/**
	 * @param imperial
	 *            the imperial to set
	 */
	public void setImperial(boolean imperial) {
		this.imperial = imperial;
	}
}
