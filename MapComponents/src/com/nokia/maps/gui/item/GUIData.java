package com.nokia.maps.gui.item;

import javax.microedition.lcdui.Graphics;

/**
 * 
 * A <code>GUIData</code> is a low level Graphic rendered UI class
 * which is rendered on screen.
 *
 */
public interface GUIData {

	/**
	 * Draws the GUI item onto the display.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param anchor
	 */
	void paint(Graphics g, int x, int y, int anchor);

	/**
	 * @return the width of the GUI item in pixels
	 */
	int getWidth();

	/**
	 * @return the height of the GUI item in pixels.
	 */
	int getHeight();

}