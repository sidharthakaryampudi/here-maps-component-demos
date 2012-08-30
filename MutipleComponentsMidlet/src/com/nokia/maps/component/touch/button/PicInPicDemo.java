package com.nokia.maps.component.touch.button;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.touch.button.PictureInPictureButton;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.orientation.MapOrientator;


/**
 * Demonstrates adding a picture in picture button to the map, the small Pic-in-Pic
 * will track the central location of the main map as it is panned/zoomed.
 */
public class PicInPicDemo extends MapCanvas implements CommandListener {

    private final Command EXIT = new Command("Exit", Command.EXIT, 1);

    private final MIDlet midlet;

    /**
     * Pic-in-Pic Constructor
     * @param display
     * @param midlet
     */
    public PicInPicDemo(Display display, MIDlet midlet) {
        super(display);

        addCommand(EXIT);
        setCommandListener(this);

        // Allows landscape or Portrait Mode where applicable.
        MapOrientator.init(midlet);

        // Removes unnecessary map components.
        map.removeMapComponent(map.getMapComponent("DownloadIndicator"));

        // Add the button - the try catch is in case the Images fail to load.
        try {

            PictureInPictureButton picInPicButton = new PictureInPictureButton();

            map.addMapComponent(picInPicButton);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // Set up the map.
        GeoBoundingBox europe = new GeoBoundingBox(new GeoCoordinate(65, -5, 0),
                new GeoCoordinate(35, 15, 0));

        map.zoomTo(europe, false);

        // Ensure that the Zoom Buttons are at the back of the display queue.
        MapComponent component = map.getMapComponent("ZoomImgComponent");

        map.removeMapComponent(component);
        map.addMapComponent(component);
        this.midlet = midlet;

    }

    /**
     * If the map overlay is unable to retrieve map tiles, or the emulator is
     * unable to connect to the internet to verify the map copyrights, an error
     * will be thrown here.
     * 
     * @param description
     * @param detail
     * @param critical
     */
    public void onMapUpdateError(String description, Throwable detail,
            boolean critical) {
        // TODO Auto-generated method stub

        // This should be replaced by more comprehensive error handling..
        System.out.println(description);
        throw new RuntimeException(detail.getMessage());
    }

    /**
     * This means that the all tiles are present and completely rendered with
     * all objects present.
     */
    public void onMapContentComplete() {// TODO Auto-generated method stub
    }

    /**
     * Standard Command button interaction.
     * 
     * @param c
     * @param d
     */
    public void commandAction(final Command c, Displayable d) {
        if (c == EXIT) {
            midlet.notifyDestroyed();
        }
    }
}
