package com.nokia.maps.example.kml;


import com.nokia.maps.kml.Feature;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;


/**
 * Displays the description and name of a given {@link Feature} in Balloon
 * text format.
 */
public class KMLBalloonView extends Form {

    final static Command OK = new Command("Ok", Command.OK, 1);
    private HTMLAdaptor htmlAdaptor = new HTMLAdaptor();

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
     */
    public KMLBalloonView(Feature feature) {
        super("");
        if (feature.getDescription() != null) {
            // If there is adescription, use the name as the title.
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
