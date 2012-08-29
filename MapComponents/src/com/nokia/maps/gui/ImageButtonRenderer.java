package com.nokia.maps.gui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.GUIData;

public class ImageButtonRenderer extends GUIItemRenderer {


	//private final ImageButton imageButton;
	private Image image;
	private Image highlightImage;


	/**
	 * Default constructor.
	 * @param image
	 */
	public ImageButtonRenderer(Image image, Image highlight) {
		this(image, highlight, null);
	}
	
	
	/**
	 * constructor, the various inputs define the style of the image button.
	 * 
	 * @param image
	 * @param highlight
	 * @param background
	 */
	public ImageButtonRenderer(Image image, Image highlight, BackgroundBox background) {
		super( );
		setGUIItem(new ImageButton());
		setGUIBackground( background);
		setImage(image);
		setHighlightImage(highlight != null  ? highlight: image);

	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public Image getHighlightImage() {
		return highlightImage;
	}

	public void setHighlightImage(Image image) {
		this.highlightImage = image;
	}
	
	public void setHighlight(boolean highlight) {
		super.setHighlight(highlight);
		((ImageButton) getGUIData()).setHighlight(highlight);
	}

	


	private class ImageButton implements GUIData {
		
		private boolean highlight;
		
		public void paint(Graphics g, int x, int y, int anchor) {
			g.drawImage(isHighlight()? getHighlightImage() : getImage(), x, y,
					Graphics.TOP | Graphics.LEFT);
		}

		public int getWidth() {
			return getImage().getHeight();
		}

		public int getHeight() {
			return getImage().getWidth();
		}
		
		public boolean isHighlight() {
			return highlight;
		}

		public void setHighlight(boolean highlight) {
			this.highlight = highlight;
		}
	};

}
