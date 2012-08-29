package com.nokia.maps.component.touch;

import javax.microedition.lcdui.Graphics;

import com.nokia.maps.gui.GUIItemRenderer;
import com.nokia.maps.gui.item.GUIData;
import com.nokia.maps.map.Point;

public abstract class GUITouchComponent extends TouchComponent {

	private final GUIItemRenderer renderer;

	public GUITouchComponent(GUIItemRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Gets a renderer to paint the associated visual item on screen.
	 * 
	 * @return The associated GUI Item Renderer
	 */

	protected GUIItemRenderer getRenderer() {
		return renderer;
	}

	protected void highlightGUI(boolean highlight) {
		renderer.setHighlight(highlight);
	}

	

	protected void touchAt(Point point) {
		highlightGUI(false);
	}

	protected boolean isGUIActive(int x, int y) {
		return getRenderer().isHighlight();
	}

	public boolean onTouchEventStart(int x, int y) {
		// The default here is to highlight the button.
		highlightGUI(true);
		return super.onTouchEventStart(x, y);
	}

	public boolean onDragEvent(int x, int y) {
		// The default here is to stop highlighting
		// assuming the item is visible.
		if (isGUIVisible()) {
			highlightGUI(false);
		}
		return super.onDragEvent(x, y);
	}

	/**
	 * Gets the defined visual element to place on screen.
	 * 
	 * @return The associated GUI Item
	 */
	protected GUIData getGUIData() {
		return renderer.getGUIData();
	}

	protected boolean isGUITouched(int x, int y) {
		if (isGUIVisible()) {
			Point anchor = getRenderer().getAnchor();
			if (x > anchor.getX()
					&& x < anchor.getX() + getGUIData().getWidth()
					&& y > anchor.getY()
					&& y < anchor.getY() + getGUIData().getHeight()) {
				return true;
			}

		}
		return false;
	}

	protected boolean isGUIVisible() {
		return getGUIData() != null;
	}

	protected Point getTouchPoint(int x, int y) {
		Point anchor = getRenderer().getAnchor();
		return new Point(x - anchor.getX(), y - anchor.getY());
	}

	// from MapComponent
	public void paint(Graphics g) {
		renderer.paint(g);
	}
}
