package com.nokia.maps.component;


import com.nokia.maps.map.EventListener;


/**
 * 
 * This is a skeleton Event listener to avoid having to 
 * handle all Event types. The Event Listener does not
 * consume any events.
 *
 */
public abstract class AbstractEventListener implements EventListener {

    public static final boolean EVENT_NOT_CONSUMED = false;
    public static final boolean EVENT_CONSUMED = true;

    /**
     * Called when a key is pressed.
     *
     * @param keyCode the key code
     * @return <code>true</code> if key was consumed, <code>false</code>  otherwise.
     */
    public boolean keyPressed(int keyCode, int gameAction) {
        return EVENT_NOT_CONSUMED;
    }

    /**
     * Called when a key is released.
     *
     * @param keyCode the key code
     * @return <code>true</code> if key was consumed, <code>false</code>  otherwise.
     */
    public boolean keyReleased(int keyCode, int gameAction) {
        return EVENT_NOT_CONSUMED;
    }

    /**
     * Called when a key is repeated.
     *
     * @param keyCode the key code
     * @return <code>true</code> if key was consumed, <code>false</code>  otherwise.
     */
    public boolean keyRepeated(int keyCode, int gameAction, int repeatCount) {
        return EVENT_NOT_CONSUMED;
    }

    /**
     * Called when a pointer drag event occurs.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return <code>true</code> if pointer event was consumed, <code>false</code>  otherwise.
     */
    public boolean pointerDragged(int x, int y) {
        return EVENT_NOT_CONSUMED;
    }

    /**
     * Called when a pointer pressed event occurs.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return <code>true</code> if pointer event was consumed, <code>false</code>  otherwise.
     */
    public boolean pointerPressed(int x, int y) {
        return EVENT_NOT_CONSUMED;
    }

    /**
     * Called when a pointer released event occurs.
     *
     * @param x the x coordinate
     * @param y  the y coordinate
     * @return <code>true</code> if pointer event was consumed, <code>false</code>  otherwise.
     */
    public boolean pointerReleased(int x, int y) {
        return EVENT_NOT_CONSUMED;
    }

}
