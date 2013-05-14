/**
* Copyright (c) 2013 Nokia Corporation.
*/

package com.nokia.maps.ui.helpers;


import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.midlet.MIDlet;


/**
 * Helper class for opening other applications via hypertext links
 */
public class HypertextLinkRunner implements ItemCommandListener {

    private final MIDlet midlet;
    private static HypertextLinkRunner instance;
    public static final Command COMMAND = new Command("Hyperlink", Command.ITEM,
            1);

    /**
     * This method returns the helper class for opening other applications via hypertext links
     * @param midlet the associated midlet
     * @return the HypertextLinkRunner helper
     */
    public static HypertextLinkRunner getInstance(MIDlet midlet) {
        if (null == midlet) {
            throw new IllegalArgumentException("Midlet cannot be null.");
        }
        if (null == instance) {
            instance = new HypertextLinkRunner(midlet);
        }
        return instance;
    }

    /**
     * Constructor
     * @param midlet  the associated midlet
     */
    private HypertextLinkRunner(MIDlet midlet) {
        this.midlet = midlet;
    }

    /**
     * If a link is touched, open up the appropriate app.
     * @param c
     * @param item
     */
    public void commandAction(Command c, Item item) {

        try {
            midlet.platformRequest(((URLItem) item).getURL());
        } catch (ConnectionNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
