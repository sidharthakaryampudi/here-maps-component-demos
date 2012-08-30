package com.nokia.maps.component.touch;


import com.nokia.maps.component.AbstractEventListener;


public class TouchEventHandler extends AbstractEventListener {

    private final TouchComponent component;

    public TouchEventHandler(TouchComponent component) {
        this.component = component;
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

        // If the point on screen is within the area defined by the
        // associated GUI Item, then it needs to be handled.
        return (component.isGUITouched(x, y) && component.isGUIActive(x, y))
                ? component.onTouchEventEnd(x, y)
                : component.isGUITouched(x, y);
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
    public boolean pointerDragged(int x, int y) {
        return component.onDragEvent(x, y);
    }

    /**
     * Called when a pointer pressed event occurs.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return <code>true</code> if pointer event was consumed
     */
    public boolean pointerPressed(int x, int y) {
        return (component.isGUITouched(x, y))
                ? component.onTouchEventStart(x, y)
                : component.onTouchEventClear(x, y);
    }

}


;
