package com.nokia.mid.ui;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;


public class IconCommand extends Command {

    public static final int ICON_ADD_CONTACT = 0;
    public static final int ICON_BACK = 1;
    public static final int ICON_OPTIONS = 2;
    public static final int ICON_SEND_SMS = 3;
    public static final int ICON_OK = 4;

    public IconCommand(String shortLabel, String longLabel, int commandType, int priority, int iconId) {
        // compiled code
        super(longLabel, iconId, iconId);
    }

    public IconCommand(String label, int commandType, int priority, int iconId) {
        // compiled code
        super(label, iconId, iconId);
    }

    public IconCommand(String shortLabel, String longLabel, Image unselectedIcon, Image selectedIcon, int commandType, int priority) {
        // compiled code
        super(longLabel, longLabel, priority, priority);
    }

    public IconCommand(String label, Image unselectedIcon, Image selectedIcon, int commandType, int priority) {
        // compiled code
        super(label, priority, priority);
    }

    public int getIconId() {
        // compiled code
        throw new RuntimeException("Compiled Code");
    }

    public Image getUnselectedIcon() {
        // compiled code
        throw new RuntimeException("Compiled Code");
    }

    public Image getSelectedIcon() {
        // compiled code
        throw new RuntimeException("Compiled Code");
    }
}
