package com.nokia.maps.gui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Font;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.ContextMenu;
import com.nokia.maps.map.Point;

/**
 * The Context Menu renderer is responsible for painting a nicely styled box
 * with the Context Menu items within it.
 */
public class ContextMenuRenderer extends GUIItemRenderer {

	private static final int Y_OFFSET = 3;
	

	private final ContextMenu contextMenu;
	private Point touchedAt;

	
	

	/**
	 * Context Menu constructor, the various inputs define the style of the
	 * menu.
	 * 
	 * @param border
	 * @param margin
	 * @param borderColor
	 * @param highlightColor
	 * @param background
	 * @param textColor
	 * @param font
	 */
	public ContextMenuRenderer( int textColor, Font font, BackgroundBox background) {
		super();
		
		contextMenu = new ContextMenu(background.getHighlightColor(), background.getBorderColor(), textColor);
		setGUIBackground( background);
		hidePopup();
	}

	/**
	 * 
	 * Default Constructor.
	 * 
	 * @return A default tooltip renderer, Blue text, White background and a
	 *         thin black border.
	 */
	public ContextMenuRenderer() {
		this(WHITE, Font
				.getDefaultFont(), new BackgroundBox( DARK_GREY, CYAN, MID_GREY, MID_GREY) );
	}

	public void setPreferredDimensions(int maxWidth, int maxHeight) {
		contextMenu.setMaxWidth(maxWidth - POP_UP_MARGINS);
		contextMenu.setMaxHeight((maxHeight/2) - POP_UP_MARGINS );
	}
	
	public void setHighlight(boolean highlight) {
		contextMenu.setHighlight(highlight);
	}
	
	public boolean isHighlight( ) {
		return contextMenu.isHighlight();
	}

	public int touchAt(int x, int y) {
		touchedAt = new Point(x,y);
		touchedAt.translate( -getAnchor().getX(), -getAnchor().getY());
		return contextMenu.touchAt(touchedAt);		
	}
	
	public void draggedTo(int x, int y){
		Point draggedTo =  new Point(x,y);
		draggedTo.translate( -getAnchor().getX(), -getAnchor().getY() - touchedAt.getY());
		contextMenu.incrementShift(- draggedTo.getY() );	
	}



	/**
	 * Clears a context menu so it is no longer displayed.
	 */
	public void hidePopup() {
		setGUIItem(null);
	}

	public void showPopUp(Point anchor, ChoiceGroup menuItems) {
		if (menuItems == null) {
			hidePopup();
			return;
		}
		contextMenu.setMenuItems(menuItems);
		contextMenu.clearShift();
		setGUIItem(contextMenu);
		
		setAnchor(anchor.getX()- getGUIData().getWidth() / 2, anchor.getY()
				+ Y_OFFSET + getBorderAndMargin());
	}
	
	private int getBorderAndMargin(){
		return getGUIBackground() != null ? getGUIBackground().getBorderAndMargin() : 0;
	}
}
