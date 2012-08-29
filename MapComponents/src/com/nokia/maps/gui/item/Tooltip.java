package com.nokia.maps.gui.item;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * This class is responsible for writing the tooltip text to the screen,
 * wrapping around if necessary.
 */
public class Tooltip extends PopUpGUItem {

	private String[] texts;

	private final Font font;

	private final int textColor;

	/**
	 * Constructor.
	 * 
	 * @param tooltipText
	 *            the text to display.
	 * @param font
	 *            the font to use.
	 * @param maxWidth
	 *            the maximum width of the tooltip.
	 */
	public Tooltip(Font font, int textColor) {
		this.font = font;
		this.textColor = textColor;


	}

	/**
	 * 
	 * @param text
	 * @param maxLineWidth
	 * @return A series of Strings each of which will render in less than the
	 *         maximum line width.
	 */
	private String[] splitTextIntoLines(String text) {
		if (font.stringWidth(text) < getMaxWidth()) {
			return new String[] { text };
		}
		return multipleLinesOfText(text, getMaxWidth());
	}

	/**
	 * Split the text into individual words and recombines them so that the
	 * lines of text will display correctly on screen.
	 * 
	 * @param text
	 * @param maxLineWidth
	 * @return
	 */
	private String[] multipleLinesOfText(String text, int maxLineWidth) {
		Vector lines = new Vector();
		StringBuffer buf = new StringBuffer();
		String[] words = splitIntoWords(text);

		for (int i = 0; i < words.length; i++) {
			
			if (font.stringWidth(buf.toString() + words[i]) > maxLineWidth) {
				lines.addElement(buf.toString());
				buf = new StringBuffer(words[i]);
			} else {
				buf.append(words[i]);
			}
			buf.append(" ");
		}
		lines.addElement(buf.toString());

		String[] textArray = new String[lines.size()];
		lines.copyInto(textArray);

		return textArray;
	}

	/**
	 * Implementation of a String.split() function. The words are split by
	 * whitespace.
	 * 
	 * @param tooltipText
	 * @return an array of words with the white space removed.
	 */
	private String[] splitIntoWords(String tooltipText) {
		// Splt the tooltip text into words.
		Vector words = new Vector();

		int index = tooltipText.indexOf(" ");
		while (index >= 0) {
			words.addElement(tooltipText.substring(0, index));
			tooltipText = tooltipText.substring(index + 1);
			index = tooltipText.indexOf(" ");
		}
		// Get the last node
		words.addElement(tooltipText);
		String[] wordsArray = new String[words.size()];
		words.copyInto(wordsArray);

		return wordsArray;
	}

	/**
	 * Helper fucntion to determine the widest width the generated lines of text
	 * will take up. This can be used to calculate how wide the box around it
	 * must be.
	 * 
	 * @return
	 */
	private int calculateWidth() {
		int maxWidth = 0;
		for (int i = 0; i < texts.length; i++) {
			int textWidth = font.stringWidth(texts[i]);
			maxWidth = (maxWidth > textWidth) ? maxWidth : textWidth;
		}
		return maxWidth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nokia.maps.component.tooltip.Renderable#paint(javax.microedition.
	 * lcdui.Graphics, int, int, int)
	 */
	public void paint(Graphics g, int x, int y, int anchor) {
		g.setFont(font);
		g.setColor(textColor);
		g.setClip(x,y, getWidth(), getHeight());
		for (int i = 0; i < texts.length; i++) {
			g.drawString(texts[i], x, y - getShift() + (i * font.getHeight()), anchor);
		}
	}

	
	public void setTooltipText(String text) {
		texts = splitTextIntoLines(text);
		setWidth (calculateWidth());
		int height = font.getHeight() * texts.length;
		setHeight(height);
		setMaxShift(Math.abs(getHeight() - height));
	}
}
