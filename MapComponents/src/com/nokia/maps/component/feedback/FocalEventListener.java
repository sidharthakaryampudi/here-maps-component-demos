package com.nokia.maps.component.feedback;


/**
 * 
 * Callback interface for the FocalObserverComponent
 *
 */
public interface FocalEventListener {

    /**
     * Callback when a Map object is at the centre of the screen
     * @param focus - the data associated with the focal object.
     */
    void onFocusChanged(Object focus);
}
