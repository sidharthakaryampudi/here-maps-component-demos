/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.touch.button;


import javax.microedition.lcdui.Graphics;

import com.nokia.maps.component.touch.GUITouchComponent;
import com.nokia.maps.gui.GUIItemRenderer;
import com.nokia.maps.map.MapDisplay;


/**
 * The Button Component displays a static button on the MapDisplay.
 */
public abstract class ButtonComponent extends GUITouchComponent {

    private static final String VERSION = "1.0";
    private static final int DEFAULT_OFFSET = 10;

    private final int positioning;
    private int xOffset = DEFAULT_OFFSET;
    private int yOffset = DEFAULT_OFFSET;

    public ButtonComponent(int positioning, GUIItemRenderer renderer) {
        super(renderer);
        this.positioning = positioning;
    }

    /**
     * When a map is attached to the component, calculate the position to draw
     * the button on screen.
     */
    public void attach(MapDisplay map) {
        super.attach(map); // Obtain a reference to the current map.
        positionGUIOnScreen();
    }

    /**
     * Calculate the position of the component based on the current map.
     * 
     */
    protected void positionGUIOnScreen() {
        int x = xOffset;
        int y = yOffset;

        if ((positioning & Graphics.BOTTOM) != 0) {
            y = map.getHeight() - getGUIData().getHeight() - yOffset;
        }
        if ((positioning & Graphics.RIGHT) != 0) {
            x = map.getWidth() - getGUIData().getWidth() - xOffset;
        }
        getRenderer().setAnchor(x, y);

    }

    // from MapComponent
    public String getVersion() {
        return VERSION;
    }

    // from MapComponent
    public void mapUpdated(boolean zoomChanged) {// For static button components, the default is to do nothing
        // when the map is updated.
    }

    /**
     * Sets how many pixels away from the corner a button will be displayed.
     * 
     * @param offset
     *            the offset to set
     */
    public void setOffset(int x, int y) {
        this.xOffset = x;
        this.yOffset = y;
    }

}
