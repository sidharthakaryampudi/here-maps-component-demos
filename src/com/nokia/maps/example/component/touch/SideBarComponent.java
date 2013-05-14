/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.component.touch;


import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.nokia.maps.component.touch.TouchEventHandler;
import com.nokia.maps.component.touch.VisibleTouchComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;


/**
 *
 * A Custom Map Component which displays a sidebar holding a series of menu options.
 * Unlike the IconCommand Bar it is expected that these items will fire a series
 * of actions. The control can be dragged onto the screen and hidden to maximise
 * the map displayed.
 *
 */
public class SideBarComponent extends VisibleTouchComponent {

    /**
     * Unique ID for the Sidebar Component.
     */
    public static final String ID = "SideBar";

    private int selectedIndex;
    private Command[] commands;
    private final Displayable displayable;
    private final CommandListener commandListener;

    /**
     * Constructor initialisation.
     * @param mapCanvas
     * @param commandListener
     * @throws IOException
     */
    public SideBarComponent(Displayable mapCanvas,
            CommandListener commandListener) throws IOException {
        super(ID,
                new SideBarRenderer(
                Image.createImage("/component/sidebar/handle.png")));
        this.displayable = mapCanvas;
        this.commandListener = commandListener;
    }

    /**
     * Sets up a series of Actions to display as Icons
     * @param selected
     * @param unselected
     * @param labels
     * @param commands
     */
    public void setCommands(Image[] selected, Image[] unselected,
            String[] labels, Command[] commands) {
        getSideBarUI().setUnselected(unselected);
        getSideBarUI().setSelected(selected);
        getSideBarUI().setLabels(labels);
        this.commands = commands;
    }

    private SideBarRenderer getSideBarUI() {
        return ((SideBarRenderer) getRenderer());
    }

    /**
     * attached a Map to the Map Component.
     *
     * @param map
     */
    public void attach(MapDisplay map) {
        super.attach(map);
        getSideBarUI().setPreferredDimensions(map.getWidth(), map.getHeight());
    }

    /**
     * Called when a pointer press has started.
     * @param x
     * @param y
     * @return <code>true</code> if pointer event was consumed
     */
    protected boolean onTouchEventStart(int x, int y) {
        selectedIndex = getSideBarUI().touchAt(x, y);
        return super.onTouchEventStart(x, y);
    }

    /**
     * Called when a drag event occurs.  If the handle is dragged, open/close the side bar.
     * @param x
     * @param y
     * @return <code>true</code> if pointer event was consumed
     */
    protected boolean onDragEvent(int x, int y) {

        if (getSideBarUI().isHandleTouched(x, y)) {
            getSideBarUI().draggedTo(x, y);
        }
        return super.onDragEvent(x, y);
    }

    /**
     * Whether the Sidebar of the Handle has been touched (ie.e two hit areas)
     */
    protected boolean isTouched(int x, int y) {

        return getSideBarUI().isHandleTouched(x, y)
                || getSideBarUI().isSidebarTouched(x, y);

    }

    /**
     * Fire an Action if selected.
     */
    public void touchAt(Point point) {
        if (selectedIndex != -1 && commands != null
                && commands.length > selectedIndex) {
            commandListener.commandAction(commands[selectedIndex], displayable);
        }
        super.touchAt(point);
    }

    public boolean onLongPressEvent(int x, int y) {
        // Stop the bubbling of LongPress Events if we have been touched.
        // A Drag can be recognized as a Long Press if a User is slow to move.
        if (getSideBarUI().isHandleTouched(x, y)) {

            getSideBarUI().setVisible(!getSideBarUI().isVisible());
            return TouchEventHandler.EVENT_CONSUMED;
        }
        return getSideBarUI().isSidebarTouched(x, y);
    }

}
