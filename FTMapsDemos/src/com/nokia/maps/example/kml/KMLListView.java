package com.nokia.maps.example.kml;


import com.nokia.maps.kml.Document;
import com.nokia.maps.kml.Feature;
import com.nokia.maps.kml.Folder;
import com.nokia.maps.kml.PlaceMark;
import com.nokia.maps.kml.component.KMLResultSet;
import com.nokia.maps.map.MapContainer;
import com.nokia.maps.map.MapObject;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;


/**
 *
 * List View for a KML {@link Document}.
 */
public class KMLListView extends Form {

    public final static Command OK = new Command("Ok", Command.OK, 1);
    public final static Command CANCEL = new Command("Cancel", Command.CANCEL, 2);
    private int choiceCount = 0;
    private final Feature[] features;
    private final MapContainer container;
    private final ChoiceGroup choice;

    /**
     * Constructor
     * @param resultSet the KMLResultSet data to be summarized.
     */
    public KMLListView(KMLResultSet resultSet) {
        super("");
        container = resultSet.getContainer();
        features = resultSet.getFeatures();

        //
        String[] items = new String[features.length];
        Image[] icons = new Image[features.length];

        for (int i = 0; i < features.length; i++) {
            String name = features[i].getName() != null
                    ? features[i].getName()
                    : features[i].getType();

            if (features[i].getType().equals(PlaceMark.ELEMENT)) {
                name = " " + name;
            }
            items[i] = name;
        }

        choice = new ChoiceGroup("", Choice.MULTIPLE, items, icons);
        setCheckboxes(true);
        append(choice);
        addCommand(OK);
        addCommand(CANCEL);
    }

    /**
     * Helper function to iteratively set a series of check boxes.
     * @param checked
     */
    private void setCheckboxes(boolean checked) {
        for (int i = 0; i < choice.size(); i++) {
            choice.setSelectedIndex(i, checked);
        }
    }

    /**
     * Updates  the visibility of map objects of current KMLResultSet,
     * based on the choices made by the user
     * @return the updated mapContainer.
     */
    public MapContainer updateContainer() {
        choiceCount = 0;
        setVisibleGeometries(new MapObject[] { container}, true);
        return container;
    }

    /**
     * Recursive function to update the visibility of mapObjects based on  the
     * choices made and the visibility of any parent &lt;Folder&gt;
     * @param geometries the mapObjects to be displayed.
     * @param parentVisible  whether the parent &lt;Folder&gt; is visible.
     */
    private void setVisibleGeometries(MapObject[] geometries, boolean parentVisible) {
        for (int i = 0; i < geometries.length; i++) {

            boolean visible = choice.isSelected(choiceCount) && parentVisible;

            choice.setSelectedIndex(choiceCount, visible);
            choiceCount++;
            if (Folder.ELEMENT.equals(features[choiceCount - 1].getType())
                    || Document.ELEMENT.equals(
                            features[choiceCount - 1].getType())) {
                setVisibleGeometries(
                        ((MapContainer) geometries[i]).getAllMapObjects(),
                        visible);
            }
            geometries[i].setVisible(visible);
        }
    }
}
