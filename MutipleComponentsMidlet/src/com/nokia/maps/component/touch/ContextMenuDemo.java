package com.nokia.maps.component.touch;


import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.GeoBoundingBox;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.component.feedback.FocalEventListener;
import com.nokia.maps.component.feedback.FocalObserverComponent;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.ContextMenuComponent;
import com.nokia.maps.gesture.GestureHandler;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.orientation.MapOrientator;


/**
 * Demonstrates adding a series of Context Menus to the map. When a Map Marker
 * is centered on the screen, a context menu appears below it. The menu is
 * clickable and scrollable.
 */
public class ContextMenuDemo extends MapCanvas implements CommandListener,
        FocalEventListener {

    private final Command EXIT = new Command("Exit", Command.EXIT, 1);

    private final ContextMenuComponent contextMenus;
    private final FocalObserverComponent focalComponent;

    private final MIDlet midlet;

    /**
     * Set up a series of commands for the Context menus to use.
     */
    private final Command DO1 = new Command("item 1", Command.ITEM, 1);
    private final Command DO2 = new Command("item 2", Command.ITEM, 2);
    private final Command DO3 = new Command("item 3", Command.ITEM, 3);
    private final Command DO4 = new Command("item 4", Command.ITEM, 4);
    private final Command DO5 = new Command("item 5", Command.ITEM, 5);
    private final Command DO6 = new Command("item 6", Command.ITEM, 6);
    private final Command DO7 = new Command("item 7", Command.ITEM, 7);
    private final Command DO8 = new Command("item 8", Command.ITEM, 8);
    private final Command[] commands = {
        DO1, DO2, DO3, DO4, DO5, DO6, DO7, DO8 };

    private final Alert alert;
    private String currentFocus;

    /**
     * Constructor for the Context Menu Demo.
     * 
     * @param display
     * @param midlet
     */
    public ContextMenuDemo(Display display, MIDlet midlet) {
        super(display);

        addCommand(EXIT);
        setCommandListener(this);

        alert = new Alert("");
        alert.setTimeout(1000);

        // Register for flick and pinch events in the whole canvas area
        // Potentially Context Menus handle the Flick event.
        GestureHandler.init(this);

        // Allows landscape or Portrait Mode where applicable.
        MapOrientator.init(midlet);

        // Removes unnecessary map components.
        map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
        map.removeMapComponent(map.getMapComponent("DefaultCursor"));

        // Add the context Menu component FIRST so that it is processed LAST.
        contextMenus = new ContextMenuComponent(this, this);
        map.addMapComponent(contextMenus);

        // Add the Focal component SECOND to feed the info bubble component
        // ABOVE
        focalComponent = new FocalObserverComponent(this);
        map.addMapComponent(focalComponent);

        // Finally add the Centering Component to feed the focal observer
        map.addMapComponent(new CenteringComponent(this));

        // Now we can set up the markers..
        addMarkerData(new GeoCoordinate(40.4, -3.683333, 0), "Madrid");
        addMarkerData(new GeoCoordinate(51.477811d, -0.001475d, 0), "London");
        addMarkerData(new GeoCoordinate(60.170833, 24.9375, 0), "Helsinki");
        addMarkerData(new GeoCoordinate(59.949444, 10.756389, 0), "Oslo");

        addMarkerData(new GeoCoordinate(45.4375, 12.335833, 0), "Venice");

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
     * Helper function to add markers and prime the Focal Observer with data.
     * 
     * @param coord
     * @param text
     */
    private void addMarkerData(GeoCoordinate coord, String text) {
        MapStandardMarker marker = mapFactory.createStandardMarker(coord);

        focalComponent.addData(marker, text);

        map.addMapObject(marker);
    }

    /**
     * The callback from the focal observer sets up the Context Menu. This sets
     * up a list of Menu items with an optional title.
     */
    public void onFocusChanged(Object focus) {

        currentFocus = (String) focus;

        if (currentFocus != null) {

            ChoiceGroup list1 = new ChoiceGroup(currentFocus, Choice.POPUP);

            list1.append("item1", null);
            list1.append("item2", null);
            list1.append("item3", null);
            list1.append("item4", null);
            list1.append("item5", null);
            list1.append("item6", null);
            list1.append("item7", null);
            list1.append("item8", null);

            contextMenus.addData(list1, commands);
        } else {

            contextMenus.clear();
        }
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
        // Handle the Context menu command presses.
        alert.setString(currentFocus + " " + c.getLabel() + " was pressed");
        display.setCurrent(alert);
    }
}
