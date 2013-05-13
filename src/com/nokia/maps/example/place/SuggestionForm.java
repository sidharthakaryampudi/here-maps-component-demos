package com.nokia.maps.example.place;


import com.nokia.maps.common.GeoCoordinate;
import com.nokia.places.PlaceFactory;
import com.nokia.places.request.SuggestionRequest;
import com.nokia.places.request.SuggestionRequestListener;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.TextField;


/**
 * This demonstrates usage of the suggestion service.
 */
public class SuggestionForm extends Form implements SuggestionRequestListener,
        ItemStateListener {

    final static Command FREE_TEXT_SEARCH = new Command("Ok", Command.OK, 1);
    private final ChoiceGroup suggestions;
    private final TextField freeText;
    private GeoCoordinate searchLocation;
    private SuggestionRequest suggestionRequest;
    private volatile boolean blockFurtherRequests;
    private final static Object lock = new Object();

    /**
     * Default constructor
     * @param hint the initial location to base the search around.
     */
    public SuggestionForm(GeoCoordinate hint) {
        super("");
        this.searchLocation = hint;
        freeText = new TextField("Enter search term", "", 100, TextField.ANY);
        suggestions = new ChoiceGroup("suggestions", ChoiceGroup.POPUP);

        initializeForm();
    }

    /**
     * Sets up the form with two boxes and a listener.
     */
    private void initializeForm() {
        append(freeText);
        append(suggestions);
        setItemStateListener(this);
    }

    /**
     * If the free text has changed, make another suggestion request.
     * If the suggestions have been selected, update the free text fiedl.
     */
    public synchronized void itemStateChanged(Item item) {
        if (item == freeText) {
            makeSuggestionRequest();
        } else if (item == suggestions && suggestions.getSelectedIndex() > -1) {
            copySuggestionToFreeText();
        }
    }

    /**
     * Copies the currently selected suggestion to the free text field.
     */
    private void copySuggestionToFreeText() {

        synchronized (lock) {
            String text = suggestions.getString(suggestions.getSelectedIndex());

            if (text != null && (text.equals(freeText) == false)) {
                freeText.setString(text);
                addCommand(SuggestionForm.FREE_TEXT_SEARCH);
            }
            blockFurtherRequests = false;
        }
    }

    /**
     * Makes an asynchronous suggestion request. User can continue typing in the
     * meantime.
     */
    private void makeSuggestionRequest() {
        if (freeText.getString().length() > 0) {
            addCommand(SuggestionForm.FREE_TEXT_SEARCH);
            if (!blockFurtherRequests && suggestionRequest == null) {
                blockFurtherRequests = true;
                suggestionRequest = PlaceFactory.getInstance().createSuggestionRequest();
                suggestionRequest.getSuggestions(freeText.getString(),
                        searchLocation, this);
            }

        } else {
            removeCommand(SuggestionForm.FREE_TEXT_SEARCH);
        }
    }

    public void onRequestComplete(SuggestionRequest request, String[] result) {

        synchronized (lock) {
            suggestions.deleteAll();

            for (int i = 0; i < result.length; i++) {
                suggestions.append(result[i], null);
            }
        }
        blockFurtherRequests = false;
        suggestionRequest = null;
    }

    public void onRequestError(SuggestionRequest request, Throwable error) {

        synchronized (lock) {
            suggestions.deleteAll();
        }
        blockFurtherRequests = false;
        suggestionRequest = null;
    }

    /**
     * The free text to make a places search on.
     * @return the free text search.
     */
    public String getSearchText() {
        return freeText.getString();
    }

    /**
     * @return the searchLocation
     */
    public GeoCoordinate getSearchLocation() {
        return searchLocation;
    }

    /**
     * @param searchLocation
     *            the searchLocation to set
     */
    public void setSearchLocation(GeoCoordinate searchLocation) {
        this.searchLocation = searchLocation;
    }
}
