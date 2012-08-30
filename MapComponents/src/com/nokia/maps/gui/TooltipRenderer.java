package com.nokia.maps.gui;


import javax.microedition.lcdui.Font;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.Tooltip;
import com.nokia.maps.map.Point;


/**
 * The tooltip renderer is responsible for painting a nicely styled box with the
 * tooltip text within it.
 */
public class TooltipRenderer extends GUIItemRenderer {

    protected final Tooltip tooltip;
    private static final int Y_OFFSET = 3;
	
    /**
     * Default constructor.
     */
    public TooltipRenderer() {
        this(WHITE, SMALL_FONT,
                new BackgroundBox(MID_GREY, MID_GREY, PALE_GREY, PALE_GREY));
    }

    /**
     * Tooltip constructor, with the various inputs define the style of the tooltip.
     * 
     * @param border
     * @param margin
     * @param borderColor
     * @param background
     * @param textColor
     * @param font
     */
    public TooltipRenderer(int textColor, Font font, BackgroundBox background) {
        super();
        tooltip = new Tooltip(font, textColor);
        setGUIBackground(background);
        clearTooltip();
    }

    /**
     * Clears a tooltip so it is no longer displayed.
     */
    public void clearTooltip() {
        setGUIItem(null);
    }

    /**
     * Sets the location and text for a tooltip.
     * @param anchor
     * @param tooltipText
     */
    public void setTooltip(Point anchor, String tooltipText) {
        if (tooltipText == null) {
            clearTooltip();
            return;
        }
        tooltip.setTooltipText(tooltipText);
        tooltip.clearShift();
        setGUIItem(tooltip);

        super.setAnchor(anchor.getX() - getGUIData().getWidth() / 2,
                anchor.getY() + Y_OFFSET + getBorderAndMargin());
    }

    public void setPreferredDimensions(int maxWidth, int maxHeight) {
        tooltip.setMaxWidth(maxWidth - POP_UP_MARGINS);
        tooltip.setMaxHeight((maxHeight / 2) - POP_UP_MARGINS);
    }
	
    private int getBorderAndMargin() {
        return getGUIBackground() != null
                ? getGUIBackground().getBorderAndMargin()
                : 0;
    }
}
