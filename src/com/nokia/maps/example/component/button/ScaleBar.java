/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.button;


import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.button.TextButton;
import com.nokia.maps.component.ui.BackgroundBox;
import com.nokia.maps.component.ui.RGBColor;
import com.nokia.maps.map.Point;


/**
 *
 * Scale bar component displays a scale in Kilometers or Miles
 */
public class ScaleBar extends TextButton {

    private static final Font SMALL_FONT = Font.getFont(Font.FACE_PROPORTIONAL,
            Font.STYLE_PLAIN, Font.SIZE_SMALL);

    /**
     * Unique ID for the Scale Bar Component.
     */
    public static final String ID = "ScaleBar";
    private int y_coord;
    private int x_coord_start;
    private int x_coord_end;
    private boolean imperial;
    private double currentZoom = -1;
    private final int scaleBarColor;

    private final static int[] SCALE_IN_METRES = new int[] {
        2000000, 1000000, 600000, 300000, 150000, 75000, 30000, 20000, 10000,
        5000, 2000, 1000, 500, 250, 100, 50, 25, 12 };
    private final static String[] SCALE_IN_METRES_TEXT = new String[] {
        "2000 km", "1000 km", "600 km", "300 km", "150 km", "75 km", "30 km",
        "20 km", "10 km", "5 km", "2 km", "1 km", "500 m", "250 m", "100 m",
        "50 m", "25 m", "12m" };
    private final static int[] SCALE_IN_IMPERIAL = new int[] {
        4828000, 2414000, 1207000, 643737, 321868, 160934, 80467, 40233, 24140,
        16093, 8046, 3218, 1609, 457, 228, 91, 45, 22 };
    private final static String[] SCALE_IN_IMPERIAL_TEXT = new String[] {
        "3000 mi", "1500 mi", "750 mi", "400 mi", "200 mi", "100 mi", "50 mi",
        "25 mi.", "15 mi", "10 mi", "5 mi", "2 mi", "1 mi", "500 yds", "250 yds",
        "100 yds", "50 yds", "75 ft" };

    /**
     * Default constructor to place a scale bar on the Bottom Right of the map.
     */
    public ScaleBar() {
        this(Graphics.BOTTOM | Graphics.RIGHT);
        imperial = ApplicationContext.getInstance().isImperialUnits();
    }

    /**
     * Constructor to allow the scale bar to be placed in any corner
     * @param anchor the corner of the Map to set (see {@link Graphics})
     */

    public ScaleBar(int anchor) {
        this(anchor, RGBColor.WHITE, SMALL_FONT,
                new BackgroundBox(RGBColor.MID_GREY, RGBColor.CYAN,
                RGBColor.PALE_GREY, RGBColor.CYAN));
    }

    /**
     * Contructor allowing the developer to set all attributes of the scale bar.
     * @param anchor  the corner of the Map to set (see {@link Graphics})
     * @param textColor scalebar text color
     * @param font scale bar font
     * @param background scale bar background color.
     */
    public ScaleBar(int anchor, int textColor, Font font,
            BackgroundBox background) {
        super(ID, anchor, textColor, font, "", background);
        scaleBarColor = textColor;
    }

    /**
     * When the map is updated, we need to alter the scale bar.
     *
     * @param zoomChanged
     */
    public void mapUpdated(boolean zoomChanged) {

        double zoom = map.getZoomLevel();

        if (currentZoom != zoom) {
            setText(
                    isImperial()
                            ? getLegend(zoom, SCALE_IN_IMPERIAL_TEXT)
                            : getLegend(zoom, SCALE_IN_METRES_TEXT));

        }

        positionUIOnScreen();

        x_coord_start = getRenderer().getAnchor().getX()
                + BackgroundBox.TEXT_MARGIN;
        y_coord = getRenderer().getAnchor().getY()
                + getRenderer().getUI().getHeight() - 3;

    }

    private static String getLegend(double zoom, String[] legends) {
        return (zoom <= legends.length - 1) ? legends[(int) zoom] : "";
    }

    private void calculateBar(double zoom, int[] barLengths) {
        if (zoom <= barLengths.length - 1) {

            GeoCoordinate start = map.pixelToGeo(
                    new Point(x_coord_start, y_coord));

            x_coord_end = x_coord_start + 10;
            while (map.pixelToGeo(new Point(x_coord_end, y_coord)).distanceTo(
                    start)
                            < barLengths[(int) zoom]) {
                x_coord_end++;
                if (x_coord_end > getRenderer().getUI().getWidth()) {
                    break;
                }
            }

        }
    }

    public void paint(Graphics g) {
        if ("".equals(getText()) == false) {
            super.paint(g); // This paints the legend text.

            calculateBar(map.getZoomLevel(),
                    isImperial() ? SCALE_IN_IMPERIAL : SCALE_IN_METRES);

            // This draws the scale.
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
    public void toggleButton() {
        currentZoom = -1;
        setImperial(!isImperial());
        mapUpdated(false);
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
