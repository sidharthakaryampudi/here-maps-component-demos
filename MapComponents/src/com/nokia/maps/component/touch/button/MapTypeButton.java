package com.nokia.maps.component.touch.button;


import java.io.IOException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.gui.ImageButtonRenderer;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.Point;
import com.nokia.maps.selector.MapTypeSelector;


public class MapTypeButton extends ButtonComponent {

    public static final String ID = "mapType";

    /**
     * Constructor for the Map Type Button.
     * 
     * @param display
     * @param mapCanvas
     * @throws IOException
     */
    public MapTypeButton(Display display, MapCanvas mapCanvas)
        throws IOException {
        super(Graphics.TOP | Graphics.RIGHT,
                new ImageButtonRenderer(Image.createImage("/maptype.png"),
                Image.createImage("/maptype_e.png")));
        MapTypeSelector.init(display, mapCanvas);
    }

    /**
     *Toggles the Map Selector.
     */
    protected void touchAt(Point point) {
        MapTypeSelector.toggle();
        super.touchAt(point);
    }

    public String getId() {
        return ID;
    }

}
