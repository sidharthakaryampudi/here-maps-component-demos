package com.nokia.maps.example.component;


import javax.microedition.lcdui.Graphics;

import com.nokia.maps.component.AbstractMapComponent;


/**
 * Example of a minimal  Custom MapComponent. This MapComponent
 * simply  prints hello world on the screen.
 */
public class HelloWorldComponent extends AbstractMapComponent {

    /**
     * Unique ID for the Hello World Component.
     */
    public static final String ID = "Hello";
    private static final String VERSION = "1.0";

    /**
     * Default constructor.
     */
    public HelloWorldComponent() {
        super(ID, VERSION);
    }

    public void paint(Graphics g) {
        g.setColor(0x000000);
        g.drawString("HELLO WORLD", 50, 50, Graphics.TOP | Graphics.LEFT);
    }
}
