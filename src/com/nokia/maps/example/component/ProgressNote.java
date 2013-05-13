package com.nokia.maps.example.component;


import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import com.nokia.maps.component.AbstractMapComponent;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.MapListener;


/**
 * This Map Component displays a Note over the map canvas.
 *
 */
public class ProgressNote extends AbstractMapComponent {

    private int[] data;
    private String note;
    private int width;
    private int height;
    private int x = 10;
    private int y;
    private Font f = Font.getDefaultFont();
    private Timer timer = new Timer();
    private TimerTask hide = null;

    private static final String ID = "InfoNote";
    private static final String VERSION = "1.0";
    private final MapListener listener;

    /**
     * Default Constructor.
     *
     * @param listener class which receives callback events.
     */
    public ProgressNote(MapListener listener) {
        super(ID, VERSION);
        height = f.getHeight() * 2;
        this.listener = listener;

    }

    /**
     * Callback method that is invoked when the <code>MapComponent</code> is attached to a
     * map.
     * @param map
     */
    public void attach(MapDisplay map) {
        width = map.getWidth() - 20;
        height = f.getHeight() * 2;
        y = map.getHeight() - height * 2 - 10;
        data = new int[width * height];
        for (int index = 0; index < data.length; index++) {
            data[index] = 0xA8C0C0C0;
        }
    }

    /**
     * Sets a note to display on screen.
     * @param note the text of the note to set.
     */
    public void setNote(String note) {
        this.note = note;

    }

    /**
     * Sets a note to display on screen.
     * @param note the text of the note to set.
     * @param delay how long to continue displaying the note.
     */
    public void setNote(String note, int delay) {
        if (hide != null) {
            hide.cancel();
        }
        this.note = note;
        hide = new TimerTask() {

            public void run() {
                ProgressNote.this.note = null;
                listener.onMapContentUpdated();
            }
        };
        timer.schedule(hide, delay);
    }

    public void paint(Graphics g) {

        if (note != null) {
            g.setFont(f);
            g.setColor(0x000000);
            g.drawRGB(data, 0, width, x, y, width, height, true);
            g.drawString(note, x + width / 2, y + height / 2 - f.getHeight() / 2,
                    Graphics.HCENTER | Graphics.TOP);

            g.drawRect(x, y, width, height);
        }
    }

}
