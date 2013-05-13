package com.nokia.maps.example.component.button;


import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.button.ImageButton;
import com.nokia.maps.component.ui.BackgroundBox;
import com.nokia.maps.component.ui.RGBColor;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;


/**
 * This map component will add a picture in picture button to the map, the small
 * Pic-in-Pic will track the central location of the main map as it is
 * panned/zoomed.
 */
public class Overview extends ImageButton {

    /**
     * Unique ID for the Overview Component.
     */
    public static final String ID = "pipButton";

    private final Image picInPic;
    private final Image picInPicHighlight;
    private final BackgroundBox background;
    private String mapUrl;
    private GeoCoordinate coord;
    private double thresholdDistance;

    private boolean picInPicOn;
    private boolean highlight;

    private static final String MAP_URL = "http://m.nok.it/?c=";

    /**
     * Constructor for the Map Type Button.
     *
     * @throws IOException if the button glyphs (Images) cannot be found.
     */
    public Overview() throws IOException {
        super(ID, Graphics.TOP | Graphics.LEFT,
                Image.createImage("/component/button/pip.png"),
                Image.createImage("/component/button/pip_e.png"));

        picInPic = Image.createImage("/component/button/pip.png");
        picInPicHighlight = Image.createImage("/component/button/pip_e.png");
        background = new BackgroundBox(BackgroundBox.DEFAULT_BORDER_WIDTH, 0,
                RGBColor.MID_GREY, RGBColor.CYAN, BackgroundBox.NO_FILL,
                BackgroundBox.NO_FILL);
    }

    public void attach(MapDisplay map) {
        super.attach(map);
        coord = map.getCenter();
        thresholdDistance = map.getCenter().distanceTo(
                map.pixelToGeo(
                        new Point(map.getWidth() / 4, map.getHeight() / 2)));
    }

    // from MapComponent
    public void mapUpdated(boolean zoomChanged) {
        mapUrl = getMapURL(map);

        if (zoomChanged || map.getCenter().distanceTo(coord) > thresholdDistance) {
            coord = map.getCenter();
            thresholdDistance = map.getCenter().distanceTo(
                    map.pixelToGeo(
                            new Point(map.getWidth() / 4, map.getHeight() / 2)));
            setGlyph(picInPicOn ? downloadImage(mapUrl) : picInPic);

        }
    }

    /**
     * Displays/hides the Pic-in-Pic image.
     */
    public void toggleButton() {
        picInPicOn = !picInPicOn;
        setGlyph(picInPicOn ? downloadImage(mapUrl) : picInPic);
        getRenderer().setBackground(picInPicOn ? background : null);
        positionUIOnScreen();
    }

    /**
     * Highlights the Pic-in-Pic image and/or border as necessary.
     */
    protected void highlightUI(boolean highlight) {
        setHighlightGlyph(picInPicOn ? downloadImage(mapUrl) : picInPicHighlight);
        getRenderer().setBackground(picInPicOn ? background : null);
        positionUIOnScreen();
        this.highlight = highlight;
        super.highlightUI(highlight);
    }

    /**
     * Obtains the relevant RESTful map URL to display in Pic-in-Pic.
     *
     * @param map
     * @return
     */
    private static String getMapURL(final MapDisplay map) {

        int maxZoom = (int) Math.max(map.getZoomLevel() - 5,
                map.getMinZoomLevel());

        return MAP_URL + map.getCenter().getLatitude() + ","
                + map.getCenter().getLongitude() + "&z=" + maxZoom + "&app_id="
                + ApplicationContext.getInstance().getAppID() + "&token="
                + ApplicationContext.getInstance().getToken() + "&h=72"
                + "&w=72";

    }

    public void paint(Graphics g) {
        super.paint(g);
        if (picInPicOn) {
            Point anchor = getRenderer().getAnchor();

            anchor.translate(getUIData().getWidth(), getUIData().getHeight());
            anchor.translate(-picInPic.getWidth(), -picInPic.getHeight());
            g.drawImage(highlight ? picInPicHighlight : picInPic, anchor.getX(),
                    anchor.getY(), Graphics.TOP | Graphics.LEFT);

        }
    }

    /**
     * Helper function to download a specified Image asset.
     *
     * @param url
     *            - the URL of a file to download.
     * @return an image asset.
     */
    private static Image downloadImage(String url) {

        Image im = null;
        ContentConnection c = null;
        DataInputStream dis = null;

        if (url != null) {

            try {
                try {
                    c = (ContentConnection) Connector.open(url);
                    int len = (int) c.getLength();

                    dis = c.openDataInputStream();
                    if (len > 0) {
                        byte[] data = new byte[len];

                        dis.readFully(data);
                        im = Image.createImage(data, 0, data.length);
                    }
                } catch (IOException ioe) {// Failed to read the url. Can't do
                    // anything about it, just don't
                    // update the image.
                } finally {
                    // Regardless of whether we are successful, we need to close
                    // Connections behind us. Basic Housekeeping.
                    if (dis != null) {
                        dis.close();
                    }
                    if (c != null) {
                        c.close();
                    }
                }
            } catch (IOException ioe) {// closure of connections may fail,
                // nothing we can do about it.
            }
        }

        return im;
    }
}
