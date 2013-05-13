package com.nokia.maps.ui.helpers;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;


/**
 * Simple command listener to exit the examples if the AppID and token have not
 * been set.
 *
 *
 * <p>
 * You must get your own app_id and token by registering at
 * https://api.developer.nokia.com/ovi-api/ui/registration Insert your own AppId
 * and Token, as obtained from the above URL into the Base.InitialiseAuth().
 * </p>
 */
public class ExitListener implements CommandListener {

    private MIDlet midlet;

    /**
     * Constructor
     * @param midlet the midlet to destroy.
     */
    public ExitListener(MIDlet midlet) {
        this.midlet = midlet;
    }

    public void commandAction(Command c, Displayable d) {
        midlet.notifyDestroyed();
    }
}
