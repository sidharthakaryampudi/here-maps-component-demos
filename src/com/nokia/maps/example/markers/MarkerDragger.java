package com.nokia.maps.example.markers;


import com.nokia.maps.component.AbstractMapComponent;
import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapObject;
import com.nokia.maps.map.MapStandardMarker;
import com.nokia.maps.map.Point;


/**
 * This map components allows the user to select a
 * <code>MapStandardMarker</code> and drag it across the map.
 */
public class MarkerDragger extends AbstractMapComponent implements EventListener {

    /**
     * Component ID.
     */
    public static final String ID = "markerDragger";
    private static final String VERSION = "1.0";
    private MapStandardMarker draggableMarker;
    private Point currentPoint;
    private Point currentOffset;

    private Image markerIcon;

    /**
     * Constructor
     *
     */
    public MarkerDragger() {
        super(ID, VERSION);
        // load image resource from MIDlet's jar file
        try {
            markerIcon = Image.createImage("/nma_res/mask.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the {@link EventListener} instance if one exists.
     *
     * @return the event listener associated with this <code>MapComponent</code>
     *         i.e. the DraggingEventsListener defined below.
     */
    public EventListener getEventListener() {
        return this;
    }

    /**
     * Renders the <code>MapComponent</code> using the <code>Graphics</code>
     * object. This component renders a mask to show a marker being
     * dragged across screen. Rendering is quicker, since the underlying map
     * is left unaffected.
     *
     * @param g
     *            the graphics object used for rendering
     */
    public void paint(Graphics g) {

        if (draggableMarker != null) {
            g.drawImage(markerIcon,
                    currentPoint.getX() - markerIcon.getWidth() / 2
                    + currentOffset.getX(),
                    currentPoint.getY() - markerIcon.getHeight()
                    + currentOffset.getY(),
                    Graphics.TOP | Graphics.LEFT);
        }
    }

    /**
     * Called when a pointer drag event occurs.
     *
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed,
     *         <code>false</code> otherwise.
     */
    public boolean pointerDragged(int x, int y) {
        if (draggableMarker != null) {
            int dx = currentPoint.getX() - x;
            int dy = currentPoint.getY() - y;

            // If the marker has been dragged more than 4 pixels
            // signal that a repaint is required.
            if ((dx * dx) + ((dy * dy)) > 16) {
                currentPoint = new Point(x, y);
            }
        }
        return (draggableMarker != null);
    }

    /**
     * Called when a pointer pressed event occurs.
     *
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed,
     *         <code>false</code> otherwise.
     */
    public boolean pointerPressed(int x, int y) {
        currentPoint = new Point(x, y);
        MapObject mapObject = map.getObjectAt(currentPoint);

        if (mapObject != null && mapObject instanceof MapStandardMarker) {
            draggableMarker = (MapStandardMarker) mapObject;
            Point markerFocus = map.geoToPixel(draggableMarker.getCoordinate());

            currentOffset = new Point(markerFocus.getX() - currentPoint.getX(),
                    markerFocus.getY() - currentPoint.getY());
            draggableMarker.setVisible(false);
        }
        return (draggableMarker != null);
    }

    /**
     * Called when a pointer released event occurs.
     *
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed,
     *         <code>false</code> otherwise.
     */
    public boolean pointerReleased(int x, int y) {
        if (draggableMarker != null) {
            Point markerFocus = new Point(
                    currentPoint.getX() + currentOffset.getX(),
                    currentPoint.getY() + currentOffset.getY());

            draggableMarker.setCoordinate(map.pixelToGeo(markerFocus));
            draggableMarker.setVisible(true);
            draggableMarker = null;
            return true;
        }
        return false;
    }

    /**
     * Called when a key is pressed.
     *
     * @param keyCode the key code
     * @param gameAction the gameAction
     * @return true if key was consumed
     */
    public boolean keyPressed(int keyCode, int gameAction) {
        return false;
    }

    /**
     * Called when a key is released.
     *
     * @param keyCode
     *            the key code
     * @param gameAction the gameAction
     * @return true if key was consumed
     */
    public boolean keyReleased(int keyCode, int gameAction) {
        return false;
    }

    /**
     * Called when a key is repeated.
     *
     * @param keyCode
     *            the key code
     * @param gameAction the gameAction
     * @param repeatCount the repeat count
     * @return true if key was consumed
     */
    public boolean keyRepeated(int keyCode, int gameAction, int repeatCount) {
        return false;
    }

}
