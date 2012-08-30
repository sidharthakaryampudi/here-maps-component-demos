package com.nokia.maps.gui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.gui.item.BackgroundBox;
import com.nokia.maps.gui.item.GUIData;

/**
 * The Text Button renderer is responsible for painting a nicely styled box
 * with text on it.
 */
public class TextButtonRenderer extends GUIItemRenderer {

	public static final int TEXT_MARGIN = 5;
	private String text = "";
	private final Font font;
	private final int textColor;
	
	

	public TextButtonRenderer(String text) {
		this(WHITE, STANDARD_FONT, text, null);
	}

	public TextButtonRenderer(int textColor, Font font, String text, BackgroundBox background) {
		super();
		this.font = font;
		this.textColor = textColor;
		setGUIItem( new TextButton());
		setGUIBackground(  background);
		setText(text);

	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	private class TextButton implements GUIData {
		public void paint(Graphics g, int x, int y, int anchor) {
			if ("".equals(getText()) == false) {
				g.setColor(textColor);
				g.setFont(font);
				g.drawString(getText(), x + TEXT_MARGIN, y, Graphics.TOP
						| Graphics.LEFT);
			}
		}

		public int getHeight() {
			return font.getHeight() + 4;
		}

		public int getWidth() {
			return font.stringWidth(text) + (2 * TEXT_MARGIN);
		}

	};

}
