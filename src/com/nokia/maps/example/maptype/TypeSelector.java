/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.maptype;


import com.nokia.maps.component.touch.button.ImageButton;
import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.map.MapCanvas;


/**
 * This map component allows the user to switch between the five standard map
 * types. The button is a simple toggle.
 *
 *
 */
public class TypeSelector extends ImageButton {

    /**
     * Unique ID for the Type Selector Component.
     */
    public static final String ID = "TypeSelector";

    /**
     * Constructor for the Map Type Button.
     *
     * @param display
     * @param mapCanvas
     * @throws IOException
     */
    public TypeSelector(Display display, MapCanvas mapCanvas)
        throws IOException {
        super(ID, Graphics.TOP | Graphics.RIGHT,
                Image.createImage("/component/button/maptype.png"),
                Image.createImage("/component/button/maptype_e.png"));
        TypeSelectorUI.init(display, mapCanvas);
    }

    /**
     * Toggles the Map Selector.
     */
    public void toggleButton() {
        TypeSelectorUI.toggle();
    }

}
