/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.button;


import com.nokia.maps.component.touch.button.TextButton;
import com.nokia.maps.component.ui.BackgroundBox;
import com.nokia.maps.component.ui.RGBColor;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;


/**
 * This is a simple text button which fires a command when clicked.
 * It can be used as an alternative to the command menu for touch displays.
 * As a fallback for non-touch devices, a standard Command Menu item is added
 * instead.
 */
public class ButtonCommand extends TextButton {

    private final MapCanvas mapCanvas;
    private final CommandListener commandListener;
    private final Command buttonCommand;

    private static final Font SMALL_FONT = Font.getFont(Font.FACE_PROPORTIONAL,
            Font.STYLE_PLAIN, Font.SIZE_SMALL);

    /**
     * Constructor
     * @param anchor the corner to place the button in.
     * @param mapCanvas the associated map canvas
     * @param commandListener the command listener for this button.
     * @param command the command to fire when the button is pressed.
     */
    public ButtonCommand(int anchor, MapCanvas mapCanvas,
            CommandListener commandListener, Command command) {
        super(command.getLabel(), anchor, RGBColor.WHITE, SMALL_FONT,
                command.getLabel(),
                new BackgroundBox(RGBColor.BLACK, RGBColor.CYAN,
                RGBColor.DARK_GREY, RGBColor.CYAN));

        this.buttonCommand = command;
        this.mapCanvas = mapCanvas;
        this.commandListener = commandListener;

    }

    public void attach(MapDisplay map) {
        super.attach(map);
        if (!mapCanvas.hasPointerEvents()) {
            mapCanvas.addCommand(buttonCommand);
        }
    }

    public void detach(MapDisplay map) {
        super.detach(map);
        if (!mapCanvas.hasPointerEvents()) {
            mapCanvas.removeCommand(buttonCommand);
        }
    }

    public void paint(Graphics g) {
        if (mapCanvas.hasPointerEvents()) {
            super.paint(g);
        }
    }

    public void toggleButton() {
        commandListener.commandAction(buttonCommand, mapCanvas);
    }

}
