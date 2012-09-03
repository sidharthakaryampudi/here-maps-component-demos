package com.nokia.maps.component.touch;


import java.util.Hashtable;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import com.nokia.maps.component.MapFocus;
import com.nokia.maps.gui.ContextMenuRenderer;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.Point;


/**
 * A Custom MapComponent which displays a context menu.
 */
public class ContextMenuComponent extends GUITouchComponent {

    public static final String ID = "PopupMenu";
    private static final String VERSION = "1.0";
    private final Hashtable popupMenus;
    private final Hashtable commands;

    private final MapCanvas mapCanvas;
    private final CommandListener commandListener;

    private ChoiceGroup currentPopup;
    private Command[] currentCommands;
    private int selectedIndex;

    /**
     * Default Constructor.
     */
    public ContextMenuComponent(MapCanvas mapCanvas, CommandListener listener) {
        super(new ContextMenuRenderer());
        popupMenus = new Hashtable();
        commands = new Hashtable();
        commandListener = listener;
        this.mapCanvas = mapCanvas;
    }

    /**
     * Housekeeping method to remove any data associations.
     */
    public void clear() {
        commands.clear();
        popupMenus.clear();
    }

    /**
     * Associates a Context Menu to a map object.
     * 
     * @param mo
     * @param choiceGroup
     */
    public void addData(MapObject mo, ChoiceGroup choiceGroup,
            Command[] commands) {
        if (mo != null) {
            popupMenus.put(mo, choiceGroup);
            this.commands.put(mo, commands);
        }
    }

    /**
     * Associates a Context Menu to the map object found at the center of the
     * screen.
     * 
     * @param choiceGroup
     */
    public void addData(ChoiceGroup choiceGroup, Command[] commands) {
        addData(MapFocus.getInstance().objectAtMapCenter(map), choiceGroup,
                commands);
    }

    /**
     * Removes the association between a Context Menu and a MapObject.
     * 
     * @param mo
     */
    public void removeData(MapObject mo) {
        popupMenus.remove(mo);
        commands.remove(mo);
    }

    /**
     * Attaches a Map to the Map Component.
     * 
     * @param map
     */
    public void attach(MapDisplay map) {
        super.attach(map);
        getContextMenuGUI().setPreferredDimensions(map.getWidth(),
                map.getHeight());
    }

    private ContextMenuRenderer getContextMenuGUI() {
        return ((ContextMenuRenderer) getRenderer());
    }

    // from MapComponent
    public String getId() {
        return ID;

    }

    // from MapComponent
    public String getVersion() {
        return VERSION;
    }

    // overrides TouchComponent
    protected void touchAt(Point point) {
        mapCanvas.onMapContentUpdated();
        if (selectedIndex != -1 && currentCommands != null
                && currentCommands.length > selectedIndex) {
            commandListener.commandAction(currentCommands[selectedIndex],
                    mapCanvas);
        }
        super.touchAt(point);
    }

    /**
     * handles the touch start event by remembering the menu item touched.
     */
    protected boolean onTouchEventStart(int x, int y) {
        selectedIndex = getContextMenuGUI().touchAt(x, y);
        return super.onTouchEventStart(x, y);
    }

    /**
     * Handles the touch cleared event by making the ContextMeny disappear.
     */
    protected boolean onTouchEventClear(int x, int y) {
        if (currentPopup != null) {
            hideContextMenu();
        }
        return super.onTouchEventClear(x, y);
    }

    /**
     * Handles the onDrag event by  shifting the menu items.
     */
    protected boolean onDragEvent(int x, int y) {
        // Drag the menu items if visible.
        if (isGUIVisible()) {
            highlightGUI(false);

            getContextMenuGUI().draggedTo(x, y);

            return TouchEventHandler.EVENT_CONSUMED;
        }
        return super.onDragEvent(x, y);
    }

    /**
     * Handles the Flick event by shifting the menu items based on the flick speed.
     */
    protected boolean onFlickEvent(int x, int y, float direction, int speed,
            int speedX, int speedY) {
        if (isGUIVisible()) {
            getContextMenuGUI().flick(speedY);
            return TouchEventHandler.EVENT_CONSUMED;
        }
        return super.onFlickEvent(x, y, direction, speed, speedX, speedY);
    }

    /**
     * When the map is updated, we need to check if one of the MapObjects held
     * in the Hashtable has the current focus. If so, the Context Menu is
     * displayed, the Context Menu is cleared otherwise.
     * 
     * @param zoomChanged
     */

    public void mapUpdated(boolean zoomChanged) {

        MapObject focusMO = MapFocus.getInstance().objectAtMapCenter(map);

        if (focusMO != null && popupMenus.containsKey(focusMO)) {
            showContextMenu(focusMO);
        } else {
            if (currentPopup != null) {
                hideContextMenu();
            }
        }
    }

    private void hideContextMenu() {
        getContextMenuGUI().hidePopup();
        currentPopup = null;
        currentCommands = null;
    }

    private void showContextMenu(MapObject focusMO) {
        currentPopup = ((ChoiceGroup) popupMenus.get(focusMO));
        currentCommands = ((Command[]) commands.get(focusMO));
        getContextMenuGUI().showPopUp(map.geoToPixel(map.getCenter()),
                currentPopup);
    }

}
