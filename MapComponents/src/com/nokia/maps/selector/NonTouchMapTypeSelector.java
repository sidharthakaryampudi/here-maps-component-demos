package com.nokia.maps.selector;


import java.io.IOException;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;


/**
 * The <code>NonTouchMapTypeSelector</code> uses an <code>EXCLUSIVE ChoiceGroup</code>
 * to switch between Map Types. This is a modal Form. The user must select a map choice and press
 * the OK button to confirm their choice. The CANCEL button allows the user to
 * return without updating the map.
 * 
 */
public class NonTouchMapTypeSelector extends MapTypeSelector implements
        CommandListener {

    private final ChoiceForm form;
    private final Display display;

    private final static Command OK = new Command("Ok", Command.OK, 1);
    private final static Command CANCEL = new Command("Cancel", Command.CANCEL,
            1);

    /**
     * Set up the Choice Form and remember the display so we can switch back to
     * the map.
     * 
     * @param display
     */
    protected NonTouchMapTypeSelector(Display display) throws IOException {

        super();
        this.display = display;
        form = new ChoiceForm();
        form.setCommandListener(this);

    }

    /** Set the labels for the Choice group items */
    protected void setLabels(String[] labels) {
        form.setMapTypes(labels);

    }

    /**
     * Switch between the ChoiceGroup form and the MapCanvas.
     */
    protected void setVisible(boolean visible) {
        if (visible) {
            display.setCurrent(form);
        } else {
            display.setCurrent(mapCanvas);
        }
    }
	
    /**
     * Obtains the state of the Map selector
     * @return <code>true</code> if the Map type selector is visible, false otherwise.
     */
    protected boolean isVisible() {
        return form.isShown();
    }

    /**
     * Handle the ChoiceGroup Commands and return control to the mapCanvas. Also
     * Update the Map and if OK has been pressed.
     */
    public void commandAction(Command c, Displayable d) {

        if (c == NonTouchMapTypeSelector.OK) {
            mapCanvas.getMapDisplay().setBaseMapType(form.getChoice());
        }
        setVisible(false);
    }

    /**
     * 
     * ChoiceForm is a simple Modal dialog box for switching between various options.
     *
     */
    private class ChoiceForm extends Form {

        private ChoiceGroup mapTypes;

        public ChoiceForm() {
            super("");
            addCommand(OK);
            addCommand(CANCEL);

        }

        protected void setMapTypes(String[] labels) {
            this.mapTypes = new ChoiceGroup("", Choice.EXCLUSIVE, labels,
                    UNSELECTED_IMAGES);
            this.deleteAll();
            append(mapTypes);
        }

        protected int getChoice() {
            return mapTypes.getSelectedIndex();
        }

    }

}
