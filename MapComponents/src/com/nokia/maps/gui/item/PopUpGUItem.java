package com.nokia.maps.gui.item;

/**
 * 
 * Extended basic GUIData for pop-up GUI Items such as tooltips.
 *
 */
public abstract class PopUpGUItem implements GUIData {
	
	private int height;
	private int maxHeight;
	private int maxWidth;
	private int width;
	private boolean highlight = false;
	
	private int shift = 0;
	private int maxShift = 0;

	
	
	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	public int getMaxHeight() {
		return maxHeight;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}

	public int getHeight() {
		return Math.min(maxHeight, height);
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void incrementShift(int shiftBy) {
	
		if (shift + shiftBy >= 0 && shift + shiftBy < maxShift) {
			shift = shift + shiftBy;
		}
	
	}

	public void clearShift() {
		shift = 0;
	}
	
	public void setMaxShift(int maxShift) {
		this.maxShift = maxShift;
	}
	
	public int  getShift() {
		return shift;
	}
	

}
