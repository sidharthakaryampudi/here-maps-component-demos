package com.nokia.maps.gui.item;


import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.map.Point;


/**
 * 
 * Data defining the Context Menu items.
 * 
 */
public class ContextMenu extends PopUpGUItem {

    private ChoiceGroup menuItems;
	
    private static final int ROW_SPACING = 7;
    private static final int TEXT_MARGIN = 5;

    private final int headerBackground;
    private final int textColor;
    private final int highlightColor;
    private int selectedIndex = -1;

    public ContextMenu(int highlightColor, int headerBackground, int textColor) {

        this.highlightColor = highlightColor;
        this.headerBackground = headerBackground;
        this.textColor = textColor;

    }

    /**
     * 
     * @param point
     * @return Which context menu item was pressed.
     */
    public int touchAt(Point point) {
        selectedIndex = -1;

        int y = point.getY() + getShift();

        if (menuItems.getLabel() != null) {
            y = y - Font.getDefaultFont().getHeight() - ROW_SPACING;
        }
        if (y > 0) {
            for (int i = 0; i < menuItems.size(); i++) {
                y = y - getRowHeight(i);
                if (y < 0) {
                    selectedIndex = i;

                    break;
                }
            }
        }
        return selectedIndex;
    }
	
    private int getRowHeight(int i) {
        if (menuItems.getImage(i) != null) {
            return Math.max(menuItems.getFont(i).getHeight(),
                    menuItems.getImage(i).getHeight())
                    + ROW_SPACING;
        }
        return menuItems.getFont(i).getHeight() + ROW_SPACING;
    }

    public void paint(Graphics g, int x, int y, int anchor) {
        int imgOff;

        if (menuItems != null) {
            if (menuItems.getLabel() != null) {
				
                g.setColor(headerBackground);
                g.fillRoundRect(x, y, getWidth(),
                        Font.getDefaultFont().getHeight() + ROW_SPACING,
                        BackgroundBox.SMALL_CORNER_ARC,
                        BackgroundBox.SMALL_CORNER_ARC);
                g.setColor(textColor);
                g.setFont(Font.getDefaultFont());
                g.drawString(menuItems.getLabel(), x + TEXT_MARGIN, y + 2,
                        anchor);
                y = y + Font.getDefaultFont().getHeight() + ROW_SPACING;
                g.setClip(x, y, getWidth(),
                        getHeight() - Font.getDefaultFont().getHeight()
                        - ROW_SPACING);
            } else {
                g.setClip(x, y, getWidth(), getHeight());
            }
            g.setColor(textColor);
			
            for (int i = 0; i < menuItems.size(); i++) {

                if (i == selectedIndex && isHighlight()) {
                    g.setColor(highlightColor);
                    g.fillRoundRect(x, y - getShift(), getWidth(),
                            getRowHeight(i), BackgroundBox.SMALL_CORNER_ARC,
                            BackgroundBox.SMALL_CORNER_ARC);
                    g.setColor(textColor);
                }
				
                if (menuItems.getImage(i) != null) {
                    g.drawImage(menuItems.getImage(i), x, y + 2 - getShift(),
                            Graphics.TOP | Graphics.LEFT);
                    imgOff = menuItems.getImage(i).getWidth();
                } else {
                    imgOff = 0;
                }
                g.setFont(menuItems.getFont(i));
				
                g.drawString(menuItems.getString(i), x + TEXT_MARGIN + imgOff,
                        y + 2 - getShift(), anchor);

                if (i > 0) {
                    g.drawLine(x, y - getShift(), x + getWidth(), y - getShift());
                }
                y = y + getRowHeight(i);
            } 
        }
    }

    public void setMenuItems(ChoiceGroup menuItems) {

        this.menuItems = menuItems;
        int height = 0;

        if (menuItems.getLabel() != null) {
            height = height + Font.getDefaultFont().getHeight() + ROW_SPACING;
        }
        for (int i = 0; i < menuItems.size(); i++) {
            height = height + getRowHeight(i);
        }
        setHeight(height);
		
        setMaxShift(Math.abs(getHeight() - height));

    }
	
    public int getWidth() {
        return getMaxWidth();
    }

}
