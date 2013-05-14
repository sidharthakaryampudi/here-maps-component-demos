/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example;


import com.nokia.maps.ui.helpers.ExitListener;
import com.nokia.maps.ui.helpers.CommandRunner;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.example.component.ProgressNote;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;


/**
 * Common functionality needed by all example applications.
 */
public final class Base implements CommandListener {

    private final Alert reconnectAlert = new Alert("Connection Error");
    private static final Command NO = new Command("No", Command.BACK, 1);
    private static final Command YES = new Command("Yes", Command.OK, 1);
    private static final Command EXIT = new Command("Exit", Command.EXIT, 1);
    private final MIDlet midlet; // for notifyDestroyed
    private final MapCanvas mapCanvas;
    private final CommandRunner commandRunner;
    private final Display display;
    protected String onProgressFail; // shown to user if error happens during a
    // command
    private ProgressNote progress;

    /**
     * This isn't the correct place to put this but this method is added here as
     * this allows the authentication parameters to be set in a single place in
     * order to allow the examples to compile and run. Please obtain your own
     * correct credentials from address shown below.
     */
    public static void InitialiseAuth() {

        // All users of Nokia APIs must obtain authentication and authorization
        // credentials, which are app_id and token. They are assigned per
        // application.
        //
        // To obtain the credentials for an application:
        //
        // 1. Open the HERE Developer Site - http://developer.here.com/ and in the
        // top-right corner click Sign in, then pick one option:
        //
        // * Log in with your HERE account credentials – you may need to
        //   create an account first.
        // * Log in with your Facebook account credentials.
        //
        // 2. Open My Apps, click Create App and follow the on-screen instructions.
        //
        //
        // Insert your own AppId and Token, as obtained from the above
        // URL into the two methods below.

        ApplicationContext.getInstance().setAppID("...");
        ApplicationContext.getInstance().setToken("...");

        //
        // Enabling Direct Utils will mean the API renders map objects faster
        // and more clearly for capable devices. This flag is not set up default.
        //
        // Use of DirectUtils has been disabled for the code examples to allow
        // them to run over a wider range of devices.
        ApplicationContext.getInstance().disableDirectUtils();
        //
        // Faster rendering can be obtained on many devices by uncommenting the
        // line of code below:
        //
        // ApplicationContext.getInstance().enableDirectUtils();


        //
        // By default, the API exposes all I/O errors whilst downloading
        // map tiles - if you wish the API to ignore any I/O errors encountered
        // whilst downloading, set the flag below as shown.
        // ApplicationContext.getInstance().setTileIOFailureIgnored(true);

    }

    /**
     * Whether to proceed with the example code. The examples will not work
     * "out of the box". A free appId and token must be added to the method
     * above.
     *
     * @param midlet
     * @return <code>true</code> if App Id and token have been supplied
     *         <code>false</code> otherwise.
     */
    public static boolean checkAuth(MIDlet midlet) {
        boolean noAuth = "...".equalsIgnoreCase(
                ApplicationContext.getInstance().getAppID())
                        || "...".equalsIgnoreCase(
                                ApplicationContext.getInstance().getToken());

        if (noAuth) {

            Display display = Display.getDisplay(midlet);
            Alert alert = new Alert("Invalid AppId and Token");

            alert.setTimeout(Alert.FOREVER);
            alert.setString(
                    "You must get your own app_id and token by registering at \n"
                            + "http://developer.here.com "
                            + "\n Insert your own AppId and Token, as obtained from the above"
                            + "URL into the Base.InitialiseAuth() method");
            Command NO_AUTH_EXIT = new Command("Exit", Command.EXIT, 1);

            alert.addCommand(NO_AUTH_EXIT);
            alert.setCommandListener(new ExitListener(midlet));
            display.setCurrent(alert);
        }

        return !noAuth;
    }

    /**
     * Standard MapCanvas example initialisation.
     *
     * @param midlet
     */
    public Base(MIDlet midlet, Display display, MapCanvas mapCanvas,
            CommandRunner commandRunner) {
        this.midlet = midlet;
        this.mapCanvas = mapCanvas;
        this.display = display;
        this.commandRunner = commandRunner;
        mapCanvas.addCommand(EXIT);
        mapCanvas.setCommandListener(this);
        progress = new ProgressNote(mapCanvas);
        mapCanvas.getMapDisplay().addMapComponent(progress);
    }

    /**
     * from CommandListener
     */
    public void commandAction(final Command c, Displayable d) {
        if (c == EXIT) {
            midlet.notifyDestroyed();
        } else if (c == YES) {
            mapCanvas.getMapDisplay().reconnect();
            display.setCurrent(mapCanvas);
        } else if (c == NO) {
            display.setCurrent(mapCanvas);
        } else {
            commandRunner.commandRun(c);
        }
    }

    /**
     * Ensure that the Zoom Buttons are at the back of the display queue. e.g.
     * behind the context menu.
     */
    protected void moveZoomButtonToBack() {
        MapComponent component = mapCanvas.getMapDisplay().getMapComponent(
                "ZoomImgComponent");

        if (component != null) {
            mapCanvas.getMapDisplay().removeMapComponent(component);
            mapCanvas.getMapDisplay().addMapComponent(component);
        }
    }

    /**
     * Hides progress dialog
     */
    protected void progressEnd() {
        onProgressFail = null;
        display.setCurrent(mapCanvas);
        progress.setNote(null);
        mapCanvas.repaint();
    }

    /**
     * Shown progress dialog
     *
     * @param note
     *            note to show
     * @param onFail
     *            text shown to user if exception occurs during progress
     */
    protected void progressStart(String note, String onFail) {
        onProgressFail = onFail;
        progress.setNote(note);
        mapCanvas.repaint();
    }

    protected void note(String note, int delay) {
        progress.setNote(note, delay);
    }

    /**
     * shows error dialog
     *
     * @param text
     *            The text to display.
     */
    protected void error(String text) {
        progressEnd();
        progress.setNote((onProgressFail != null) ? onProgressFail : text, 2000);
    }

    /**
     *
     *
     * @see com.nokia.maps.map.MapCanvas#onMapUpdateError(java.lang.String,
     *      java.lang.Throwable, boolean)
     * @param description
     *            the description of the source of the error
     * @param detail
     *            the exception detail, such as IOException etc
     * @param critical
     *            if this is critical, always true
     */
    public void onMapUpdateError(String description, Throwable detail,
            boolean critical) {
        reconnectAlert.setTimeout(Alert.FOREVER);

        StringBuffer buf = new StringBuffer(
                detail.getMessage() != null
                        ? detail.getMessage()
                        : detail.getClass().getName());

        buf.append("\nDo you wish to reconnect?");

        reconnectAlert.setTitle(
                detail.getMessage() != null
                        ? detail.getClass().getName()
                        : "Connection Error");

        reconnectAlert.setString(buf.toString());
        reconnectAlert.addCommand(NO);
        reconnectAlert.addCommand(YES);
        reconnectAlert.setCommandListener(this);

        // It may be instructive to view the stack trace to find the cause
        // of the error.
        detail.printStackTrace();

        display.setCurrent(reconnectAlert);
    }

    /**
     * This means that the all tiles are present and completely rendered with
     * all objects present.
     */
    public void onMapContentComplete() {}
}
