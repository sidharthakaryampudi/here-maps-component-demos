package com.nokia.maps.component.touch;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.nokia.maps.gui.SideBarRenderer;
import com.nokia.maps.map.MapDisplay;
import com.nokia.maps.map.Point;

/**
 * 
 * A Custom Map Component which displays a sidebar holding a series of menu options.
 * Unlike the IconCommand Bar it is expected that these items will fire a series
 * of actions. The control can be dragged onto the screen and hidden to maximise
 * the map displayed.
 *
 */
public class SideBarComponent extends GUITouchComponent {

	private static final String VERSION = "1.0";
	public static final String ID = "SideBar";

	private int selectedIndex;
	private Command[] commands;
	private final Displayable displayable;
	private final CommandListener commandListener;

	/**
	 * Constructor initialisation.
	 * @param mapCanvas
	 * @param commandListener
	 * @throws IOException
	 */
	public SideBarComponent(Displayable mapCanvas,
			CommandListener commandListener) throws IOException {
		super(new SideBarRenderer(Image.createImage("/handle.png")));
		this.displayable = mapCanvas;
		this.commandListener = commandListener;
	}

	/**
	 * Sets up a series of Actions to display as Icons
	 * @param selected
	 * @param unselected
	 * @param labels
	 * @param commands
	 */
	public void setCommands(Image[] selected, Image[] unselected,
			String[] labels, Command[] commands) {
		getSideBarGUI().setUnselected(unselected);
		getSideBarGUI().setSelected(selected);
		getSideBarGUI().setLabels(labels);
		this.commands = commands;
	}

	private SideBarRenderer getSideBarGUI() {
		return ((SideBarRenderer) getRenderer());
	}

	public void attach(MapDisplay map) {
		super.attach(map);
		getSideBarGUI().setPreferredDimensions(map.getWidth(), map.getHeight());
	}

	/**
	 * Remember which item is being touched.
	 */
	protected boolean onTouchEventStart(int x, int y) {
		selectedIndex = getSideBarGUI().touchAt(x, y);
		return super.onTouchEventStart(x, y);
	}

	/**
	 * If the handle is dragged, open/close the side bar.
	 */
	public boolean onDragEvent(int x, int y) {
		if (getSideBarGUI().isHandleTouched(x, y)) {
			getSideBarGUI().draggedTo(x, y);
		}
		return super.onDragEvent(x, y);
	}

	/**
	 * Whether the Sidebar of the Handle has been touched (ie.e two hit areas)
	 */
	protected boolean isGUITouched(int x, int y) {
		return getSideBarGUI().isHandleTouched(x, y)
				|| getSideBarGUI().isSidebarTouched(x, y);

	}

	/**
	 * Fire an Action if selecetd.
	 */
	protected void touchAt(Point point) {
		if (selectedIndex != -1 && commands != null
				&& commands.length > selectedIndex) {
			commandListener.commandAction(commands[selectedIndex], displayable);
		}
		super.touchAt(point);
	}

	protected boolean onLongPressEvent(int x, int y) {
		// Stop the bubbling of LongPres Events if we have been touched.
		// The Drag can be recognised as a Long Press if a USer is slow to move.
		return getSideBarGUI().isSidebarTouched(x, y);
	}

	public String getId() {
		return ID;
	}

	public String getVersion() {
		return VERSION;
	}

	public void mapUpdated(boolean zoomChanged) {
		// This control does not rely on mapState.

	}

}
