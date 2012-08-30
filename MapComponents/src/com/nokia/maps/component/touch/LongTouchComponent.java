package com.nokia.maps.component.touch;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.Point;


/**
 * The focus component checks to see if an object can be found underneath the
 * touch press and sets the center of the map accordingly. This could also be
 * done using the Gesture API.
 * 
 */
public class LongTouchComponent extends TouchComponent {

    private static final String VERSION = "1.0";
    public static final String ID = "LongTouch";
    private static final long THRESHOLD = 500;

    private final Displayable displayable;
    private final CommandListener commandListener;
    private long time;

    private Command longTouchCommand;
    private GeoCoordinate touchAt;

    /**
     * Default constructor
     */
    public LongTouchComponent() {
        super();
        displayable = null;
        commandListener = null;
        setCommand(null);
    }

    /**
     * A constructor with a ready made command to fire when a long press has occurred.
     * @param mapCanvas
     * @param commandListener
     * @param command
     */
    public LongTouchComponent(MapCanvas mapCanvas,
            CommandListener commandListener, Command command) {
        super();
        this.displayable = mapCanvas;
        this.commandListener = commandListener;
        this.setCommand(command);
    }

    public String getId() {
        return ID;
    }

    public String getVersion() {
        return VERSION;
    }

    public void mapUpdated(boolean zoomChanged) {// This component does not respond to changes of map state.
    }

    public void paint(Graphics g) {// There is no visual feedback associated with this component.
    }

    /**
     * Since the Long press reacts to the whole screen, the GUI is always touched.
     */
    protected boolean isGUITouched(int x, int y) {
        return true;
    }

    /**
     * The GUI is only active if the threshold has been exceeded.
     */
    protected boolean isGUIActive(int x, int y) {
        return System.currentTimeMillis() - time > THRESHOLD;
    }

    /**
     * On a touch event, fire the associated command (if we have one).
     */
    protected void touchAt(Point point) {

        touchAt = map.pixelToGeo(point);
        if (getCommand() != null && commandListener != null
                && displayable != null) {
            commandListener.commandAction(getCommand(), displayable);
        }

    }

    /**
     * Start a time to see if the event will be handled.
     */
    public boolean onTouchEventStart(int x, int y) {
        time = System.currentTimeMillis();
        return super.onTouchEventStart(x, y);
    }

    /**
     * Reset the time. Note that the event is not consumed.
     */
    public boolean onDragEvent(int x, int y) {
        time = System.currentTimeMillis();
        return TouchEventHandler.EVENT_NOT_CONSUMED;
    }

    public Command getCommand() {
        return longTouchCommand;
    }

    public void setCommand(Command focusCommand) {
        this.longTouchCommand = focusCommand;
    }

    /**
     * 
     * @return The location the last long press occurred at.
     */
    public GeoCoordinate getTouchAt() {
        return touchAt;
    }

}
