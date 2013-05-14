/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.ui.helpers;


import javax.microedition.lcdui.Command;


/**
 * Interface to allow the Base class to bubble up Commands as necessary.
 */
public interface CommandRunner {

    /**
     *
     * @param c the command to run.
     */
    void commandRun(Command c);

}
