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

    /**
     * Sets/removes highlighting of the GUI element the fact the item is being pressed
     * @param highlight
     */
    protected void highlightGUI(boolean highlight) {
        renderer.setHighlight(highlight);
    }

    /**
     * Touches a GUI Element.
     */
    protected void touchAt(Point point) {
        highlightGUI(false);
    }

    /**
     * Checks to see whether a GUI element is active - i.e. if a touch has not been cancelled.
     */
    protected boolean isGUIActive(int x, int y) {
        return getRenderer().isHighlight();
    }

    /**
     * Highlights the GUI Item when it is touched.
     */
    public boolean onTouchEventStart(int x, int y) {
        highlightGUI(true);
        return super.onTouchEventStart(x, y);
    }

    /**
     * Stops highlighting the GUI Item since a drag event has occured.
     */
    public boolean onDragEvent(int x, int y) {
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

    /**
     * Decides whether the hit area has been touched.
     */
    protected boolean isGUITouched(int x, int y) {
        if (isGUIVisible()) {
            Point anchor = getRenderer().getAnchor();

            if (x > anchor.getX() && x < anchor.getX() + getGUIData().getWidth()
                    && y > anchor.getY()
                    && y < anchor.getY() + getGUIData().getHeight()) {
                return true;
            }

        }
        return false;
    }

    /**
     * Decides whether the GUI is visible or not.
     * @return
     */
    protected boolean isGUIVisible() {
        return getGUIData() != null;
    }
	
    /**
     * Calculates where on the GUI item the touch event occurred.
     */
    protected Point getTouchPoint(int x, int y) {
        Point anchor = getRenderer().getAnchor();

        return new Point(x - anchor.getX(), y - anchor.getY());
    }

    // from MapComponent
    public void paint(Graphics g) {
        renderer.paint(g);
    }
}
