/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.maps.component.touch.button;

import javax.microedition.lcdui.Font;

import com.nokia.maps.gui.TextButtonRenderer;
import com.nokia.maps.gui.item.BackgroundBox;

/**
 *  The Text Button displays text over a static button on the MapDisplay.
 */
public class TextButton extends ButtonComponent {

	public static final String ID = "TextButton";



	/**
	 * Default constructor for a text button component.
	 * @param anchor Which corner the button is attached to.
	 */
	public TextButton(int anchor) {
		super(anchor, new TextButtonRenderer(""));

	}

	/**
	 * Alternative constructor allowing full access to the rendering style of the button.
	 * @param anchor Which corner the button is attached to.
	 * @param border
	 * @param borderColor
	 * @param backgroundColor
	 * @param textColor
	 * @param font
	 * @param text
	 */
	public TextButton(int anchor, int textColor, Font font, String text, BackgroundBox background) {
		super(anchor, new TextButtonRenderer(textColor, font, text, background));
	}

	/**
	 * @return the text displayed on the button.
	 */
	public String getText() {
		return getTextGUI().getText();
	}

	/**
	 * Sets the text to be displayed on the button.
	 * @param text the text to set
	 */
	public void setText(String text) {
		getTextGUI().setText(text);
	}
	
	private TextButtonRenderer getTextGUI() {
		return ((TextButtonRenderer)getRenderer());
	}


	// from Map Component.
	public String getId() {
		return ID;
	}	
	
	/*protected void touchAt(Point point) {
			
	}*/

}
