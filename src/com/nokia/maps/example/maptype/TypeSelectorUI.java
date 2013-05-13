package com.nokia.maps.example.maptype;


import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

import com.nokia.maps.map.MapCanvas;


/**
 * This is a Factory pattern to create an appropriate Map Type Selector based on the
 * capabilities of the device. The implementation is hidden behind an interface
 * and accessed through static functions only.
 *
 * The preferred UI-  {@link CategoryBarTypeSelector} class relies on the presence of the
 * <code>CategoryBar</code> class from the Nokia 2.0 SDK. If the class is not present, then the
 * {@link FallbackTypeSelector} will be used.
 *
 * To ensure that the named class is available at Compile time, and additional
 * stubs jar has been added to the build path. If the {@link CategoryBarTypeSelector} class is not needed,
 * then the stubs.jar can be removed.
 */
public abstract class TypeSelectorUI {

    protected MapCanvas mapCanvas;

    /**
     * There are 5 Map types catered for, each map type has an associated image.
     * In the example The Selected and Unselected images are the same, but the
     * resources could include alternative "highlighted" Images as well if
     * desired.
     */
    protected static final Image[] SELECTED_IMAGES = new Image[5];
    protected static final Image[] UNSELECTED_IMAGES = new Image[5];

    /**
     * The Text of the commands should be sync'ed with the locale of the device.
     * Only two languages (English and Spanish) are given in this example, but
     * the mechanism can be extended as necessary.
     */
    private static final String ENGLISH_COMMAND_TEXT = "Map Type";
    private static final String[] ENGLISH_LABELS = new String[] {
        "Street Map", "Terrain", "Satellite", "Hybrid", "Transit" };

    private static final String SPANISH_COMMAND_TEXT = "Tipo de Mapa";
    private static final String[] SPANISH_LABELS = new String[] {
        "Callejero", "Terreno", "Satelite", "Hibrido", "Transporte Publico" };

    /**
     * This is a singleton to actually pass commands to the Map Type Selector.
     */
    private static TypeSelectorUI typeSelector;

    protected TypeSelectorUI() throws IOException {
        // I'm a Singleton.

        UNSELECTED_IMAGES[0] = Image.createImage("/component/maptype/normal.png");
        UNSELECTED_IMAGES[1] = Image.createImage(
                "/component/maptype/terrain.png");
        UNSELECTED_IMAGES[2] = Image.createImage(
                "/component/maptype/satellite.png");
        UNSELECTED_IMAGES[3] = Image.createImage("/component/maptype/hybrid.png");
        UNSELECTED_IMAGES[4] = Image.createImage(
                "/component/maptype/transit.png");

        SELECTED_IMAGES[0] = Image.createImage("/component/maptype/normal.png");
        SELECTED_IMAGES[1] = Image.createImage("/component/maptype/terrain.png");
        SELECTED_IMAGES[2] = Image.createImage(
                "/component/maptype/satellite.png");
        SELECTED_IMAGES[3] = Image.createImage("/component/maptype/hybrid.png");
        SELECTED_IMAGES[4] = Image.createImage("/component/maptype/transit.png");
    }

    /**
     * This is a menu Command used to display the typeSelector. Since the menu
     * Command has an associated text, it makes sense to initialise it along
     * with the Map Selector texts.
     */
    private Command commandButton;

    /**
     *
     * @return a Command Button with locale specified text for use if required.
     */
    public static Command getCommand() {
        checkInitialized();
        return typeSelector.commandButton;
    }

    /***
     * The Map Selector is initialized here. Firstly the various pictures are
     * loaded as Image resources, then the appropriate Map Selector is created.
     * Finally, the Images and Texts are passed into the Map Selector.
     *
     * @param display
     *            Used by non touch Map Selector to switch Displays
     * @param mapCanvas
     *            used to switch the map types
     *
     * @throws IOException
     *             if the Image resources cannot be found.
     */

    public static void init(Display display, MapCanvas mapCanvas)
        throws IOException {

        if (typeSelector == null) {
            try {
                // The CategoryBarTypeSelector class relies on the presence of the CategoryBar class.
                // from the Nokia 2.0 SDK. If the class is not present, then the
                // fallback UI will be used.
                //
                // To ensure that the named class is available at Compile time,
                // and additional stubs jar has been added to the build path.
                Class clazz = Class.forName(
                        "com.nokia.maps.example.maptype.CategoryBarTypeSelector");

                typeSelector = (TypeSelectorUI) clazz.newInstance();
            } catch (NoClassDefFoundError e) {
                typeSelector = new FallbackTypeSelector(display);
            } catch (Exception e) {
                // Class.forName potentially throws some fatal error
                // messages we won't handle them here for clarity, but wrap them
                // instead.
                throw new RuntimeException(
                        e.getMessage() != null ? e.getMessage() : e.toString());
            }

            // Base the menu texts on the locale of the device.

            typeSelector.setLanguage(System.getProperty("microedition.locale"));
            typeSelector.mapCanvas = mapCanvas;
        }

        return;

    }

    /**
     * Switch statement to set the language of the labels to be displayed.
     *
     * @param language
     *            a two letter MARC code defining the language to be displayed.
     */
    private void setLanguage(String language) {

        if ("es-ES".equals(language)) {
            setItemsAndCommand(SPANISH_LABELS, SPANISH_COMMAND_TEXT);
        } /* Add additional languages here */else {
            // The default language is English.
            setItemsAndCommand(ENGLISH_LABELS, ENGLISH_COMMAND_TEXT);
        }

    }

    /**
     * Helper Function to set the labels for the Menu items and the label of the
     * Menu command
     *
     * @param labels
     * @param commandText
     */
    private void setItemsAndCommand(String[] labels, String commandText) {

        setLabels(labels);
        commandButton = new Command(commandText, Command.ITEM, 1);

    }

    /**
     * Function to set the labels on the Menu Items or Category Bar
     *
     * @param labels
     */
    protected abstract void setLabels(String[] labels);

    /**
     * Function to display the ChoiceGroup Form or Category Bar
     *
     * @param visible
     */
    protected abstract void setVisible(boolean visible);

    /**
     * Obtains the state of the Map typeSelector
     *
     * @return <code>true</code> if the Map type typeSelector is visible, false
     *         otherwise.
     */
    protected abstract boolean isVisible();

    /**
     * Function to delegate any actions specified from a command listener.
     *
     * @param c
     * @param d
     */
    protected void commandAction(final Command c, Displayable d) {// The default handler does not have any commands associated with it.
    }

    /**
     * Function to allow the calling code to invoke the Map Type Selector
     * directly rather than through the Command Button.
     */
    public static void toggle() {
        checkInitialized();
        typeSelector.setVisible(!typeSelector.isVisible());
    }

    /**
     * Method to allow all button interaction of the TypeSelectorUI to be self
     * contained.
     *
     * @param c
     * @param d
     */
    public static void handleCommandAction(final Command c, Displayable d) {
        checkInitialized();
        if (c == getCommand()) {
            typeSelector.setVisible(true);
        } else {
            typeSelector.commandAction(c, d);
        }

    }

    /**
     * Ensure that the Map Selector has been initialized and throw an
     * appropriate Exception if this is not the case.
     */
    private static void checkInitialized() {
        if (typeSelector == null) {
            throw new IllegalStateException("Selector not initialized");
        }
    }

}
