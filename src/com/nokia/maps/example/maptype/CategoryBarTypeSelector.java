/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.maptype;


import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import com.nokia.mid.ui.CategoryBar;
import com.nokia.mid.ui.ElementListener;
import com.nokia.mid.ui.IconCommand;


/**
 * The CategoryBarTypeSelector uses a CategoryBar to display and switch
 * between Map Types. This displays (up to) five icons on the screen and alters
 * the map type whenever an icon is touched.
 *
 * The CategoryBarTypeSelector class relies on the presence of the CategoryBar
 * class. from the Nokia 2.0 SDK. If the class is not present, then the fallback
 * UI will be used.
 *
 * To ensure that the named class is available at Compile time, and additional
 * stubs jar has been added to the build path. If this class is not needed,
 * then the stubs.jar can be removed.
 */
public class CategoryBarTypeSelector extends TypeSelectorUI implements
        ElementListener {

    private CategoryBar cbar;
    private static final Command BACK = new IconCommand("Back", IconCommand.BACK,
            1, 1);

    /**
     * CategoryBar type selector constructor.
     * @throws IOException
     */
    public CategoryBarTypeSelector() throws IOException {
        super();
    }

    /**
     * Handle the CategoryBar Icon Commands. Switch the map type based on the
     * Icon pressed. Since the Icons have been set up using the order of the
     * Base Map type Enums, it is possible to switch by using the index directly.
     */
    public void notifyElementSelected(CategoryBar b, int index) {
        if (b == cbar) {
            mapCanvas.getMapDisplay().setBaseMapType(index);
        }
    }

    /** Set the labels for the Category Bar  icons */
    protected void setLabels(String[] labels) {
        cbar = new CategoryBar(SELECTED_IMAGES, UNSELECTED_IMAGES, labels);
        cbar.setElementListener(this);
        cbar.setTransitionSupport(true);
    }

    /**
     * Show/Hide  the Category Bar and add/remove a BACK button for the CategoryBar
     */
    protected void setVisible(boolean visible) {

        if (visible) {
            mapCanvas.addCommand(BACK);
        } else {
            mapCanvas.removeCommand(BACK);
        }
        cbar.setVisibility(visible);

    }

    /**
     * Obtains the state of the Map selector
     * @return <code>true</code> if the Map type selector is visible, false otherwise.
     */
    protected boolean isVisible() {
        return cbar.getVisibility();
    }

    /**
     * Handle the BACK button press of the Category Bar. This is the equivalent of CANCEL.
     */
    protected void commandAction(final Command c, Displayable d) {
        if (c == CategoryBarTypeSelector.BACK) {
            setVisible(false);
        }
    }

}
