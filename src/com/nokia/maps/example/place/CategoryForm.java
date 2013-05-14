/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.place;


import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.places.Category;
import com.nokia.places.PlaceFactory;
import com.nokia.places.request.CategoryRequest;
import com.nokia.places.request.CategoryRequestListener;


/**
 * Retrieves a set of Categories on start up and allows a filtered search to pick them.
 */
public class CategoryForm extends Form implements CategoryRequestListener {

    final static Command CATEGORY_SEARCH = new Command("Ok", Command.OK, 1);
    private final ChoiceGroup choice;
    private Vector categories;
    private GeoCoordinate hint;
    private Thread t;

    /**
     * Default constructor
     * @param hint the initial location to base the order of the category around.
     */
    public CategoryForm(GeoCoordinate hint) {
        super("");
        this.hint = hint;
        categories = new Vector();
        choice = new ChoiceGroup("", Choice.EXCLUSIVE);
        init();

    }

    private void init() {
        t = new Thread(new Runnable() {
            public void run() {
                makeCategoriesRequest();
            }
        });
        t.start();
    }

    /**
     * Obtains an instance of CategoryRequest and makes the request asynchronously.
     *
     */
    private void makeCategoriesRequest() {
        CategoryRequest request = PlaceFactory.getInstance().createCategoryRequest();

        // An asynchronous request is made success and failure are
        // handled in the two callback functions described below.
        request.getCategories(hint, this);
    }

    /**
     * Called when the categories request has successfully completed and a valid result
     * obtained. The details of the categories available are then displayed on screen.
     *
     * @param request the categories request that was made.
     * @param result details of the request.
     */
    public void onRequestComplete(CategoryRequest request, Category[] result) {

        for (int i = 0; i < result.length; i++) {
            if (result[i].getWithinCategory().length == 0) {
                choice.append(result[i].getTitle(), result[i].getIcon());
                categories.addElement(result[i]);
            }
        }

        this.deleteAll();
        append(choice);
        addCommand(CategoryForm.CATEGORY_SEARCH);

    }

    /**
     * Something strange occurred when the request for categories was
     * made.
     *
     * @param request the request for categories that caused the problem.
     * @param error the detail for the source of the error
     */
    public void onRequestError(CategoryRequest request, Throwable error) {
        // Simple Error handling
        System.out.println(error.toString());
    }

    /**
     * Exposes the Catgeory selected externally.
     * @return the category that has been selected.
     */
    public Category getCategory() {
        return (Category) categories.elementAt(choice.getSelectedIndex());
    }

    /**
     * @param searchLocation the hint to set. The hint is a location to help
     * ordering categories.
     */
    public void setSearchLocation(GeoCoordinate searchLocation) {

        if (hint == null || !hint.equals(searchLocation)) {
            this.hint = searchLocation;
            makeCategoriesRequest();
        }
    }
}
