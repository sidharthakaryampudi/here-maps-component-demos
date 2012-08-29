package com.nokia.maps.gui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.GUIData;
import com.nokia.maps.map.Point;

/**
 * The GUI Item renderer is responsible for painting a nicely styled box with
 * the a GUI Item within it.
 */
public abstract class GUIItemRenderer {
	
	
	public static final int WHITE = 0xFFFFFF;
	public static final int CYAN = 0x00A0A0;
	public static final int DARK_GREY = 0x404040;
	public static final int MID_GREY = 0x808080;
	public static final int PALE_GREY = 0xA0A0A0;
	public static final int BLACK = 0x000000;

	public static final Font SMALL_FONT = Font.getFont(Font.FACE_PROPORTIONAL,
			Font.STYLE_PLAIN, Font.SIZE_SMALL);
	public static final Font STANDARD_FONT = Font.getDefaultFont(); 
	
	protected static final int POP_UP_MARGINS = 20;

	private int x; 
	private int y;
	


	/**
	 * These variables define the current position and text of the tooltip.
	 *

	
	private Point borderAnchor = new Point(0, 0);
	
	private Point backgroundRect;
	private Point borderRect;
	
	*/

	private GUIData gui;
	private BackgroundBox background;
	private boolean highlight;



	/**
	 * Paints a GUI Item if one is currently defined.
	 * 
	 * @param g
	 */
	public void paint(Graphics g) {

		
		if (gui != null) {
			if (background != null) {
				// Draw the border.
				background.paint(g);
			}
						
			gui.paint(g, x, y, Graphics.TOP
					| Graphics.LEFT);
		}
		

	}

	public GUIData getGUIData() {
		// TODO Auto-generated method stub
		return gui;

	}

	protected void setGUIItem(GUIData item) {
		this.gui = item;

	}

	/**
	 * Sets up the background for display
	 * 
	 * @param anchor
	 *            Where the GUI Item should be displayed.
	 * 
	 */
	public void setAnchor(int x, int y) {

		this.x = x;
		this.y = y;
		
		if (background != null) {
			background.setAnchor(x, y, gui.getHeight(), gui.getWidth());
		}
		

	}

	public Point getAnchor() {
		return new Point(x, y);
	}
	
	
	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
		if (background != null){
			background.setHighlight(highlight);
		}
	}

	public void setGUIBackground(BackgroundBox background) {
		this.background = background;
	}
	
	public BackgroundBox getGUIBackground( ) {
		return background;
	}

}
