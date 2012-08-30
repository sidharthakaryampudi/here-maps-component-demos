package com.nokia.maps.gui.item;

import javax.microedition.lcdui.Graphics;

import com.nokia.maps.map.Point;

/**
 * 
 * Data defining the background of a GUI Item.
 *
 */
public class BackgroundBox {
	
	public static final int SMALL_CORNER_ARC = 5;

	public static final int NO_FILL = -1;
	public static final int DEFAULT_BORDER_WIDTH = 2;
	


	/**
	 * These fixed attributes hold the definition of the box surrounding the GUI
	 * Item.
	 */
	private final int margin;
	private final int borderColor;
	private final int highlightborder;
	private final int background;
	private final int highlightBackground;
	
	private final int borderAndMargin;

	/**
	 * These variables define the current position and text of the tooltip.
	 */

	private Point borderAnchor = new Point(0, 0);
	private Point backgroundAnchor;
	private Point backgroundRect;
	private Point borderRect;
	
	private boolean highlight;

	/**
	 * GUI Item constructor, the various inputs define the style of the
	 * background.
	 * 
	 * @param maxWidth
	 * @param border
	 * @param margin
	 * @param borderColor
	 * @param background
	 * @param textColor
	 * @param font
	 */
	public BackgroundBox(int border, int margin, int borderColor,
			int highlightborder,
			int background, int highlightBackground) {
		this.margin = margin;
		borderAndMargin = border + margin;
		this.borderColor = borderColor;
		this.highlightborder = highlightborder;
		this.background = background;
		this.highlightBackground = highlightBackground;

	}
	
	
	public BackgroundBox(int borderColor,
			int highlightColor,
			int background,  int highlightBackground ) {
		this(BackgroundBox.DEFAULT_BORDER_WIDTH, 2,  borderColor,
		 highlightColor,
		 background, highlightBackground);
	}

	/**
	 * Paints a GUI Item if one is currently defined.
	 * 
	 * @param g
	 */
	public void paint(Graphics g) {

		
				g.setColor(isHighlight()? highlightborder: borderColor);
				g.fillRoundRect(borderAnchor.getX(), borderAnchor.getY(),
						borderRect.getX(), borderRect.getY(), SMALL_CORNER_ARC,
						SMALL_CORNER_ARC);
				// Draw the background.
				if (background != NO_FILL){
				g.setColor(isHighlight()? highlightBackground: background);
				g.fillRoundRect(backgroundAnchor.getX(),
						backgroundAnchor.getY(), backgroundRect.getX(),
						backgroundRect.getY(), SMALL_CORNER_ARC,
						SMALL_CORNER_ARC);
				}
		

	}



	/**
	 * Sets up the background for display
	 * 
	 * @param anchor
	 *            Where the GUI Item should be displayed.
	 * 
	 */
	public void setAnchor(int x, int y, int height, int width) {

		this.borderAnchor = new Point(x - borderAndMargin,
				y - borderAndMargin);
		this.backgroundAnchor = new Point(x - margin, y
				- margin);

		this.backgroundRect = new Point(width + (margin * 2),
				height + (margin * 2));
		this.borderRect = new Point(width + (borderAndMargin * 2),
				height + (borderAndMargin * 2));

	}

	public Point getAnchor() {
		return new Point(backgroundAnchor);
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public int getBorderAndMargin() {
		return borderAndMargin;
	}
	
	public int getBorderColor() {
		return borderColor;
	}
	
	public int getHighlightColor() {
		return highlightborder;
	}
	
	public int getHighlightBackground() {
		return highlightBackground;
	}
	
}



