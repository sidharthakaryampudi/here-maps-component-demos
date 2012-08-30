package com.nokia.maps.component.feedback;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.RestrictMapComponent;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.orientation.MapOrientator;


/**
 * Demonstrates the use of the Area restriction Component. This prevents the user
 * from panning/zooming outside of a chosen area.
 */
public class RestrictDemo extends MapCanvas implements CommandListener {

    private final Command EXIT = new Command("Exit", Command.EXIT, 1);

    private final RestrictMapComponent restrict;

    private final GeoBoundingBox EUROPE_ONLY = new GeoBoundingBox(
            new GeoCoordinate(65, -10, 0), new GeoCoordinate(35, 45, 0));

    private final MIDlet midlet;

    public RestrictDemo(Display display, MIDlet midlet) {
        super(display);

        addCommand(EXIT);
        setCommandListener(this);

        // Allows landscape or Portrait Mode where applicable.
        MapOrientator.init(midlet);
		
        // Create the restriction component.
        restrict = new RestrictMapComponent(this, display, EUROPE_ONLY);
        // Adding the component to the map will enforce the restriction.
        map.addMapComponent(restrict);
	
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
        // This should be replaced by more comprehensive error handling...
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
