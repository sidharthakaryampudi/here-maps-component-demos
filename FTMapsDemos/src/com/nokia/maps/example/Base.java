package com.nokia.maps.example;


import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;

import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.component.touch.CenteringComponent;
import com.nokia.maps.component.touch.ContextMenuComponent;
import com.nokia.maps.component.touch.InfoBubbleComponent;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.orientation.MapOrientator;


/**
 * Common functionality needed by all example applications.
 */
public class Base extends MapCanvas implements CommandListener {

    private final Alert reconnectAlert = new Alert(
            "Connection Error, Reconnect?");
    private final Command NO = new Command("No", Command.BACK, 1);
    private final Command YES = new Command("Yes", Command.OK, 1);
    private final Command EXIT = new Command("Exit", Command.EXIT, 1);
    private Displayable current;
    protected MIDlet midlet; // for notifyDestroyed
    protected String onProgressFail; // shown to user if error happens during a command
    private InfoNote progress = new InfoNote();
        
    
    protected  InfoBubbleComponent infoBubble;
	protected  ContextMenuComponent contextMenus;
	protected CenteringComponent centeringComponent;

    /**
     * This isn't the correct place to put this but this method is added here
     * as this allows the authentication paramaters to be set in a single
     * place in order to allow the examples to compile and run.
     * Please obtain your own correct credentials from address shown below.
     */
    public static void InitialiseAuth() {

        // You must get your own app_id and token by registering at
        // https://api.developer.nokia.com/ovi-api/ui/registration
        // Insert your own AppId and Token, as obtained from the above
        // URL into the two methods below.

        ApplicationContext.getInstance().setAppID("K7sXZccMkHn9uAxCEy0p");
        ApplicationContext.getInstance().setToken("5hZMAu6cmKaSgsDsuKLENQ%3D%3D");

        // Due to an issue with the hostnames that are used it is not possible to use
        // international maps at this stage on the WTK emulators. The devices and Nokia
        // emulators do not suffer from this limitation.
        if ("SunMicrosystems_wtk".equals(
                System.getProperty("microedition.platform"))) {
            ApplicationContext.getInstance().setChina(true);
        }
    }

    public Base(Display display, MIDlet midlet) {
        super(display);

        this.midlet = midlet;
        addCommand(EXIT);
        setCommandListener(this);
   
		addFTBehaviour();
	}

	private void addFTBehaviour(){
		String keyboardType = System.getProperty("com.nokia.keyboard.type");
        if (keyboardType.equals("None")) {
            // full touch device detected
            com.nokia.mid.ui.VirtualKeyboard.hideOpenKeypadCommand(true);
        }

		MapOrientator.init(midlet);
		
		centeringComponent = new CenteringComponent(this, this, null);
		map.addMapComponent(centeringComponent);

		infoBubble = new InfoBubbleComponent(this, this);
		map.addMapComponent(infoBubble);

		contextMenus = new ContextMenuComponent(this, this);
		map.addMapComponent(contextMenus);
		
		map.removeMapComponent(map.getMapComponent("DefaultCursor"));
		map.removeMapComponent(map.getMapComponent("DownloadIndicator"));
		
		
		// Ensure that the Zoom Buttons are at the back of the display queue.
		MapComponent component = map.getMapComponent("ZoomImgComponent");
		map.removeMapComponent(component);
		map.addMapComponent(component);
		
		
	}
    

    /**
     * from CommandListener
     */
    public void commandAction(final Command c, Displayable d) {
        if (c == EXIT) {
            midlet.notifyDestroyed();
        } else if (c == YES) {
            map.reconnect();
            display.setCurrent(current);
        } else if (c == NO) {
            display.setCurrent(current);
        } else {
            commandRun(c);
        }
    }

    /**
     * override in derived classes to execute commands
     */
    protected void commandRun(Command c) {}

    /**
     * Hides progress dialog
     */
    protected void progressEnd() {
        onProgressFail = null;
        display.setCurrent(this);
        progress.setNote(null);
        repaint();
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
        repaint();
    }

    protected void note(String note, int delay) {
        progress.setNote(note, delay);
    }

    /**
     * shows error dialog
     */
    protected void error(String string) {
        if (onProgressFail != null) {
            string = onProgressFail;
        }
        progressEnd();
        progress.setNote(string, 2000);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.nokia.maps.map.MapCanvas#paint(javax.microedition.lcdui.Graphics)
     */
    protected void paint(Graphics g) {
        super.paint(g);
        progress.paint(g);
    }

    /**
     * (non-Javadoc)
     *
     * @see
     * com.nokia.maps.map.MapCanvas#onMapUpdateError(java.lang.String, java.lang.Throwable, boolean)
     * @param description the description of the source of the error
     * @param detail the exception detail, such as IOException etc
     * @param critical if this is critical, always true
     */
    public void onMapUpdateError(String description, Throwable detail, boolean critical) {
        reconnectAlert.setTimeout(Alert.FOREVER);
        reconnectAlert.setString("Do you wish to reconnect?");
        reconnectAlert.addCommand(NO);
        reconnectAlert.addCommand(YES);
        reconnectAlert.setCommandListener(this);

        current = display.getCurrent();

        display.setCurrent(reconnectAlert);
    }

    /**
     * This means that the all tiles are present and completely rendered with
     * all objects present.
     */
    public void onMapContentComplete() {

    }

    protected class InfoNote {

        private int[] data;
        private String note;
        private int width = getWidth() - 20;
        private int height;
        private int x = 10;
        private int y;
        private Font f = Font.getDefaultFont();
        private Timer timer = new Timer();
        private TimerTask hide = null;

        public InfoNote() {
            height = f.getHeight() * 2;
            y = getHeight() - height * 2 - 10;
            data = new int[width * height];
            for (int index = 0; index < data.length; index++) {
                data[index] = 0xA8C0C0C0;
            }
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setNote(String note, int delay) {
            if (hide != null) {
                hide.cancel();
            }
            this.note = note;
            hide = new TimerTask() {

                public void run() {
                    InfoNote.this.note = null;
                    repaint();
                }
            };
            timer.schedule(hide, delay);
        }

        public void paint(Graphics g) {
            if (note != null) {
                g.drawRGB(data, 0, width, x, y, width, height, true);
                g.drawString(note, x + width / 2/*
                         * - f.stringWidth( note ) / 2
                         */, y + height / 2 - f.getHeight() / 2,
                        Graphics.HCENTER | Graphics.TOP);
                g.drawRect(x, y, width, height);
            }
        }
    }


    /**
     * Empty MapComponent implementation. Derived classes can implement only the
     * needed methods.
     */
    public class MapComponentImpl implements MapComponent {

        public EventListener getEventListener() {
            return new EventListenerImpl();
        }

        public void attach(MapDisplay map) {}

        public void detach(MapDisplay map) {}

        public String getId() {
            return "testcomponent";
        }

        public String getVersion() {
            return "0.1";
        }

        public void mapUpdated(boolean zoomChanged) {}

        public void paint(Graphics g) {}
    }


    /**
     * Implements MapComponent's EventListener interface. Derived classes can
     * implement only the needed methods.
     */
    protected class EventListenerImpl implements EventListener {

        public boolean keyReleased(int keyCode, int gameAction) {
            return false;
        }

        public boolean keyRepeated(int keyCode, int gameAction, int repeatCount) {
            return false;
        }

        public boolean keyPressed(int keyCode, int gameAction) {
            return false;
        }

        public boolean pointerDragged(int x, int y) {
            return false;
        }

        public boolean pointerPressed(int x, int y) {
            return false;
        }

        public boolean pointerReleased(int x, int y) {
            return false;
        }
    }
}
