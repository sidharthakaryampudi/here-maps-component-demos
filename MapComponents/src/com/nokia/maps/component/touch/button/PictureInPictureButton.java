package com.nokia.maps.component.touch.button;

import java.io.DataInputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.gui.GUIItemRenderer;
import com.nokia.maps.gui.ImageButtonRenderer;
import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;

public class PictureInPictureButton extends ButtonComponent {

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
	 * @param display
	 * @param mapCanvas
	 * @throws IOException
	 */
	public PictureInPictureButton() throws IOException {
		super(Graphics.TOP | Graphics.LEFT, new ImageButtonRenderer(
				Image.createImage("/pip.png"), Image.createImage("/pip_e.png")));

		picInPic = Image.createImage("/pip.png");
		picInPicHighlight = Image.createImage("/pip_e.png");
		background = new BackgroundBox(BackgroundBox.DEFAULT_BORDER_WIDTH, 0,
				GUIItemRenderer.MID_GREY, GUIItemRenderer.CYAN,
				BackgroundBox.NO_FILL , BackgroundBox.NO_FILL);
	}

	public void attach(MapDisplay map) {
		super.attach(map);
		coord = map.getCenter();
		thresholdDistance = map.getCenter().distanceTo(
				map.pixelToGeo(new Point(map.getWidth() / 4,
						map.getHeight() / 2)));
	}

	// from MapComponent
	public void mapUpdated(boolean zoomChanged) {
		mapUrl = getMapURL(map);

		if (zoomChanged
				|| map.getCenter().distanceTo(coord) > thresholdDistance) {
			coord = map.getCenter();
			thresholdDistance = map.getCenter().distanceTo(
					map.pixelToGeo(new Point(map.getWidth() / 4, map
							.getHeight() / 2)));
			getImageGUI().setImage(
					picInPicOn ? downloadImage(mapUrl) : picInPic);

		}
	}

	/**
	 * Displays/hides the Pic-in-Pic image.
	 */
	protected void touchAt(Point point) {
		picInPicOn = !picInPicOn;
		getImageGUI().setImage(picInPicOn ? downloadImage(mapUrl) : picInPic);
		getRenderer().setGUIBackground(picInPicOn ? background : null);
		positionGUIOnScreen();
		super.touchAt(point);
	}

	/**
	 * Highlights the  Pic-in-Pic image and/or border as necessary.
	 */
	protected  void highlightGUI(boolean highlight){
		getImageGUI().setHighlightImage(
				picInPicOn ? downloadImage(mapUrl) : picInPicHighlight);
		getRenderer().setGUIBackground(picInPicOn ? background : null);
		positionGUIOnScreen();
		this.highlight = highlight;
		super.highlightGUI(highlight);
	}

	private ImageButtonRenderer getImageGUI() {
		return ((ImageButtonRenderer) getRenderer());
	}

	public String getId() {
		return ID;
	}

	/**
	 * Obtains the relevant RESTful map URL to display in Pic-in-Pic.
	 * @param map
	 * @return
	 */
	private String getMapURL(MapDisplay map) {

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
			anchor.translate(getGUIData().getWidth(), getGUIData().getHeight());
			anchor.translate(-picInPic.getWidth(), -picInPic.getHeight());
			g.drawImage(highlight ? picInPicHighlight : picInPic,
					anchor.getX(), anchor.getY(), Graphics.TOP | Graphics.LEFT);

		}
	}
	
	

	/**
	 * Helper function to download a specified Image asset.
	 * 
	 * @param url
	 *            - the URL of a file to download.
	 */
	private Image downloadImage(String url) {

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
