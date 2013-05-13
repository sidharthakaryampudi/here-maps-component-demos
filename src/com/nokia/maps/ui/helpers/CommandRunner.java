/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
