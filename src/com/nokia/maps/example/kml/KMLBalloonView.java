package com.nokia.maps.example.kml;


import com.nokia.maps.kml.Feature;
import com.nokia.maps.ui.helpers.HypertextLinkRunner;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;


/**
 * Displays the description and name of a given {@link Feature} in Balloon
 * text format.
 */
public class KMLBalloonView extends Form {

    /**
     * OK button to dismiss the Balloon View.
     */
    public final static Command OK = new Command("Ok", Command.OK, 1);
    private final HTMLAdaptor htmlAdaptor;

    /**
     * Decides whether the given feature may be displayed in a balloon view or not.
     * @param feature
     * @return <code>true</code> if the feature has an associated name or
     * description. <code>false</code> otherwise.
     */
    public static boolean isViewable(Feature feature) {
        if (feature == null) {
            return false;
        }

        String name = feature.getName() == null ? "" : feature.getName();
        String description = feature.getDescription() == null
                ? ""
                : feature.getDescription();

        return("".equals(name) == false || "".equals(description) == false);
    }

    /**
     * Constructor for the KML Balloon View.
     * The feature's &lt;description&gt; and &lt;name&gt; will be parsed to
     * extract plain text descriptions and associated images.
     * @param feature The feature to display.
     * @param hypertextRunner used to allow URL navigation
     */
    public KMLBalloonView(Feature feature, HypertextLinkRunner hypertextRunner) {
        super("");

        htmlAdaptor = new HTMLAdaptor(hypertextRunner);
        if (feature.getDescription() != null) {
            // If there is a description, use the name as the title.
            // and the description as the text.
            htmlAdaptor.parse(feature.getName());
            htmlAdaptor.parse(feature.getDescription());
            Item[] items = htmlAdaptor.getItems();

            for (int i = 0; i < items.length; i++) {
                append(items[i]);
            }
        } else {
            // If there is no description, use the name as the text.
            htmlAdaptor.parse(feature.getName());
            Item[] items = htmlAdaptor.getItems();

            for (int i = 0; i < items.length; i++) {
                append(items[i]);
            }

        }
        addCommand(OK);
    }
}
