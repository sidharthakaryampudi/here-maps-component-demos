package com.nokia.maps.gui;


import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.map.Point;


/**
 * The Infobubble renderer is responsible for painting clickable tooltip in
 * an infobubble style.
 */
public class InfoBubbleRenderer extends TooltipRenderer {

    private static final int Y_OFFSET = 20;
    private Point anchor;
    private Point touchedAt;
    private final static int[] TAIL = { 0, 1, 1, 2, 2};

    /**
     * Default constructor.
     */
    public InfoBubbleRenderer() {
        this(WHITE, SMALL_FONT,
                new BackgroundBox(DARK_GREY, DARK_GREY, BLACK, DARK_GREY));
    }

    /**
     * Infobubble constructor, with the various inputs define the style of the
     * infobubble.
     * 
     * 
     * 
     * @param textColor
     * @param font
     * @param background
     */
    public InfoBubbleRenderer(int textColor, Font font, BackgroundBox background) {
        super(textColor, font, background);

    }

    public void setTooltip(Point anchor, String tooltipText) {
        this.anchor = anchor;
        super.setTooltip(anchor, tooltipText);
        Point anchorAbove = getAnchor();

        anchorAbove.translate(0, -getGUIData().getHeight() - Y_OFFSET);

        super.setAnchor(anchorAbove.getX(), anchorAbove.getY());
    }

    public void paint(Graphics g) {

        if (getGUIData() != null) {
            drawTail(g);
            super.paint(g);			
        }

    }

    protected void drawTail(Graphics g) {
        g.setColor(
                isHighlight()
                        ? getGUIBackground().getHighlightColor()
                        : getGUIBackground().getBorderColor());
        g.fillRect(anchor.getX() - 3, anchor.getY() - 10, 7, 6);
		
        for (int i = 0; i < TAIL.length; i++) {
            g.drawLine(anchor.getX() - TAIL[i], anchor.getY() - i,
                    anchor.getX() + TAIL[i], anchor.getY() - i);
        }
    }
	
    public void touchAt(int x, int y) {
        touchedAt = new Point(x, y);
        touchedAt.translate(-getAnchor().getX(), -getAnchor().getY());
    }
	
    public void draggedTo(int x, int y) {
        Point draggedTo = new Point(x, y);

        draggedTo.translate(-getAnchor().getX(),
                -getAnchor().getY() - touchedAt.getY());
        tooltip.incrementShift(-draggedTo.getY());	
    }
	
    public void flick(int speed) {
        tooltip.incrementShift(-speed);
    }

}
