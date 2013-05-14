/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.place;


import com.nokia.maps.ui.helpers.HypertextLinkRunner;
import com.nokia.maps.ui.helpers.URLItem;
import com.nokia.places.ContactDetail;
import com.nokia.places.Contacts;
import com.nokia.places.media.Editorial;
import com.nokia.places.media.EditorialPage;
import com.nokia.places.ExtendedAttribute;
import com.nokia.places.Place;
import com.nokia.places.PlaceFactory;
import com.nokia.places.PlaceLink;
import com.nokia.places.media.ImagePage;
import com.nokia.places.media.Review;
import com.nokia.places.media.ReviewPage;
import com.nokia.places.media.request.EditorialPageRequest;
import com.nokia.places.media.request.EditorialPageRequestListener;
import com.nokia.places.media.request.ImagePageRequest;
import com.nokia.places.media.request.ImagePageRequestListener;
import com.nokia.places.request.PlaceRequest;
import com.nokia.places.request.PlaceRequestListener;
import com.nokia.places.media.request.ReviewPageRequest;
import com.nokia.places.media.request.ReviewPageRequestListener;
import com.nokia.places.request.Modifier;
import java.util.Calendar;
import java.util.Date;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;


/**
 * This demonstrates how to obtain in-depth information about an individual
 * place.
 */
public class PlaceDetailsForm extends Form implements PlaceRequestListener, ReviewPageRequestListener,
        EditorialPageRequestListener, ImagePageRequestListener {

    /**
     *  Get Editorials command
     */
    public final static Command GET_EDITORIALS = new Command("Editorials",
            Command.ITEM, 2);

    /**
     *  Get Reviews command
     */
    public final static Command GET_REVIEWS = new Command("Reviews",
            Command.ITEM, 3);

    /**
     *  Get Images command
     */
    public final static Command GET_IMAGES = new Command("Images", Command.ITEM,
            4);

    /**
     *  Get More Reviews command
     */
    public final static Command MORE_REVIEWS = new Command("More Reviews",
            Command.ITEM, 5);

    /**
     *  Get More Editorials command
     */
    public final static Command MORE_EDITORIALS = new Command("More Editorials",
            Command.ITEM, 6);

    /**
     *  Get More images command
     */
    public final static Command MORE_IMAGES = new Command("More Images",
            Command.ITEM, 7);

    /**
     *  Sharing command
     */
    public final static Command SHARE_THIS_PLACE = new Command("Share Place URL",
            Command.ITEM, 8);

    private final ItemCommandListener hypertextRunner;
    private final String[] MONTHS = {
        "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct",
        "Nov", "Dec"};
    private final PlaceFactory sf = PlaceFactory.getInstance();
    private Place place;
    private PlaceLink placeLink;
    private ReviewPage reviews;
    private EditorialPage editorials;
    private ImagePage mediaImages;

    /**
     * Form constructor.
     * @param  hypertextRunner used to allow URL navigation
     */
    public PlaceDetailsForm(HypertextLinkRunner hypertextRunner) {
        super("");
        this.hypertextRunner = hypertextRunner;
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // Common functions to add items to the display.
    //
    // ///////////////////////////////////////////////////////////////////////
    /**
     * Removes all the menu items from the display.
     */
    private void clearMenuItems() {
        removeCommand(GET_EDITORIALS);
        removeCommand(GET_IMAGES);
        removeCommand(GET_REVIEWS);
        removeCommand(MORE_EDITORIALS);
        removeCommand(MORE_REVIEWS);
        removeCommand(MORE_IMAGES);
        removeCommand(SHARE_THIS_PLACE);
    }

    /**
     * Adds a menu command if further info could be displayed.
     *
     * @param isAdd whether to add or remove this command.
     * @param command the command to add/remove.
     */
    private void addMenuItem(boolean isAdd, Command command) {
        if (isAdd) {
            addCommand(command);
        } else {
            removeCommand(command);
        }
    }

    /**
     * Adds the icon which best describes the Place.
     */
    private void addIconAndRating() {
        append(
                new ImageItem("", placeLink.getIcon(), ImageItem.LAYOUT_DEFAULT,
                placeLink.getCategory().getTitle(), Item.PLAIN));
        if (placeLink.getAverageRating().doubleValue() > 0d) {
            append(
                    new StringItem("rating",
                    String.valueOf(
                            (placeLink.getAverageRating().doubleValue() * 10)
                                    / 10),
                                    Item.PLAIN));
        }
    }

    /**
     * Alters the Title of the Form.
     */
    private void addTitle() {
        append(
                new StringItem(placeLink.getTitle(),
                placeLink.getVicinity().getText(), Item.PLAIN));
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // The Following functions are used to get the Details of a Place.
    //
    // ///////////////////////////////////////////////////////////////////////
    /**
     * Makes a request for more details about a specified place.
     * @param placeLink the place to set
     */
    public void makePlaceRequest(PlaceLink placeLink) {
        this.placeLink = placeLink;
        deleteAll();
        clearMenuItems();
        addTitle();
        addIconAndRating();

        PlaceRequest request = sf.createPlaceRequest();

        // An asynchronous request is made success and failure are
        // handled in the two callback functions described below.
        request.getPlace(placeLink.getId(), this);
    }

    /**
     * Called when place request has successfully completed and a valid result
     * obtained. The details of the place are then displayed on screen.
     *
     * @param request the request that effected the query for a {@link Place}
     * @param result details of the place that was requested by the query.
     */
    public void onRequestComplete(PlaceRequest request, Place result) {

        place = result;

        Contacts contacts = result.getContacts();

        if (contacts.getContactTypes().length == 0) {
            append(new StringItem("", "No contact info.", Item.PLAIN));
        } else {
            String[] types = contacts.getContactTypes();

            for (int i = 0; i < types.length; i++) {
                ContactDetail[] contact = contacts.getDetails(types[i]);

                for (int j = 0; j < contact.length; j++) {
                    if (Contacts.WEBSITE.equals(types[i])) {
                        append(
                                hypertextLink(contact[j].getLabel(),
                                contact[j].getValue(), contact[j].getValue()));
                    } else if (Contacts.PHONE.equals(types[i])) {
                        append(
                                hypertextLink(contact[j].getLabel(),
                                contact[j].getValue(),
                                "tel:" + contact[j].getValue()));
                    } else if (Contacts.EMAIL.equals(types[i])) {
                        append(
                                hypertextLink(contact[j].getLabel(),
                                contact[j].getValue(),
                                "mailto:" + contact[j].getValue()));
                    } else {
                        append(
                                new StringItem(contact[j].getLabel(),
                                contact[j].getValue()));
                    }
                }
            }
        }
        if (place.hasExtendedAttributes()) {
            String[] extended = place.getExtendedAttributes();

            for (int i = 0; i < extended.length; i++) {
                ExtendedAttribute extension = place.getExtendedAttributeValue(
                        extended[i]);

                append(
                        new StringItem(extension.getLabel(),
                        extension.getText().getText(), Item.PLAIN));

            }
        }

        // Add Menu items if any associated images, reviews etc are found.
        addMenuItem(place.hasEditorials(), GET_EDITORIALS);
        addMenuItem(place.hasImages(), GET_IMAGES);
        addMenuItem(place.hasReviews(), GET_REVIEWS);
        addMenuItem((place.getView() != null), SHARE_THIS_PLACE);

    }

    /**
     * Something strange occurred when the request for details of a place was
     * made.
     *
     * @param request the request that effected the query for a place details
     * @param error the detail for the source of the error
     */
    public void onRequestError(PlaceRequest request, Throwable error) {
        // Simple Error handling
        System.out.println(error.toString());
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // The Following functions are used to get the Reviews of a Place.
    //
    // ///////////////////////////////////////////////////////////////////////
    /**
     * Make a request for a page of reviews.
     *
     * @param requestMore whether we are requesting more reviews of the current
     * place or not.
     */
    public void makeReviewRequest(boolean requestMore) {
        deleteAll();
        clearMenuItems();
        addTitle();

        // An asynchronous request is made success and failure are
        // handled in the two callback functions described below.

        if (!requestMore) {
            // this is for the first page of reviews.
            ReviewPageRequest request = sf.createReviewPageRequest();

            request.getReviews(place.getId(), this);
        } else {
            // This is for the nth page of reviews.
            reviews.nextPage(this);
        }

    }

    /**
     * Called when review request has successfully completed and a valid result
     * obtained.
     *
     * @param request the request that effected the query for a page of reviews.
     * @param result a paginated collection of places that resulted from the
     * search
     */
    public void onRequestComplete(ReviewPageRequest request, ReviewPage result) {
        reviews = result;
        Review[] reviewItems = reviews.getItems();

        for (int i = 0; i < reviewItems.length; i++) {
            addReview(reviewItems[i]);
        }

        addMenuItem(reviews.hasNextPage(), MORE_REVIEWS);
    }

    /**
     * Displays the review data on screen.
     * @param review
     */
    private void addReview(Review review) {
        String title = review.getTitle();

        append(
                new StringItem(title != null ? title : "Review",
                review.getDescription().getText(), Item.PLAIN));

        StringBuffer itemText = new StringBuffer("rating");

        itemText.append(String.valueOf((review.getRating() * 10) / 10));
        itemText.append("\n");
        itemText.append("reviewer:");
        itemText.append(review.getUser().getName());
        itemText.append("\n");
        itemText.append("date:");
        itemText.append(getFormattedDate(review.getDate()));

        append(new StringItem("", itemText.toString(), Item.PLAIN));

        append(
                new StringItem("Supplier", review.getSupplierName(),
                Item.LAYOUT_DEFAULT));
        append(
                new ImageItem("", review.getSupplier().getIcon(),
                ImageItem.LAYOUT_DEFAULT, review.getSupplier().getTitle(),
                Item.PLAIN));
        if (review.getViaLink() != null && review.getViaLink().getUri() != null) {
            append(hypertextLink(review.getViaLink().getUri()));

        }
        if (doesUserHaveProfile(review)) {

            append(
                    hypertextLink("Attribution",
                    review.getAttributionText().getText(),
                    review.getUser().getProfileLink().getUri()));
        } else {
            append(
                    new StringItem("Attribution",
                    review.getAttributionText().getText(), Item.PLAIN));
        }
    }

    /**
     * Whether or not the user who created the review has an active profile to link to.
     * @param review the review
     * @return <code>true</code> if a user profile exists, <code>false</code> otherwise.
     */
    private boolean doesUserHaveProfile(Review review) {
        return (review.getUser() != null
                && review.getUser().getProfileLink() != null
                && review.getUser().getProfileLink().getUri() != null);
    }

    /**
     * Something strange occurred when the reviews request was made.
     *
     * @param request the request that effected the query for a page of reviews.
     * @param error the detail for the source of the error
     */
    public void onRequestError(ReviewPageRequest request, Throwable error) {
        // Simple Error handling
        System.out.println(error.toString());
    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // The Following functions are used to get the Editorials of a Place.
    //
    // ///////////////////////////////////////////////////////////////////////
    /**
     * Make a request for a page of editorials.
     *
     * @param requestMore whether we are requesting more editorials of the
     * current place or not.
     */
    public void makeEditorialRequest(boolean requestMore) {
        deleteAll();
        clearMenuItems();
        addTitle();

        if (!requestMore) {
            EditorialPageRequest request = sf.createEditorialPageRequest();

            request.getEditorials(place.getId(), this);
        } else {
            editorials.nextPage(this);
        }

    }

    /**
     * Called when the editorial request has successfully completed and a valid
     * result obtained.
     *
     * @param request the request that effected the query for a page of
     * editorials.
     * @param result a paginated collection of editorials that resulted from the
     * query
     */
    public void onRequestComplete(EditorialPageRequest request, EditorialPage result) {
        editorials = result;
        Editorial[] editorialItems = result.getItems();

        for (int i = 0; i < editorialItems.length; i++) {

            addEditorial(editorialItems[i]);

        }
        addMenuItem(editorials.hasNextPage(), MORE_EDITORIALS);
    }

    /**
     * Displays the editorial data on screen.
     * @param review
     */
    private void addEditorial(Editorial editorial) {
        String title = editorial.getTitle();

        append(
                new StringItem("Supplier", editorial.getSupplierName(),
                Item.LAYOUT_DEFAULT));
        append(
                new ImageItem("", editorial.getSupplier().getIcon(),
                ImageItem.LAYOUT_DEFAULT, editorial.getSupplier().getTitle(),
                Item.PLAIN));
        append(
                new StringItem(title != null ? title : "Review",
                editorial.getDescription().getText(), Item.PLAIN));
        if (editorial.getViaLink() != null
                && editorial.getViaLink().getUri() != null) {
            append(hypertextLink(editorial.getViaLink().getUri()));
        }
        append(
                new StringItem("Attribution",
                editorial.getAttributionText().getText(), Item.PLAIN));
    }

    /**
     * Something strange occurred when the editorial request was made.
     *
     * @param request the request that effected the the query for a page of
     * editorials.
     * @param error the detail for the source of the error
     */
    public void onRequestError(EditorialPageRequest request, Throwable error) {
        // Simple Error handling..
        System.out.println(error.toString());
    }

    /**
     * Reviews are returned with an unformatted date.
     *
     * @param myDate
     * @return a nicely formatted date for a review.
     */
    private String getFormattedDate(Date myDate) {
        Calendar calendar = Calendar.getInstance();
        StringBuffer buf = new StringBuffer();

        calendar.setTime(myDate);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int monthNumber = calendar.get(Calendar.MONTH);

        String month = MONTHS[monthNumber];

        int year = calendar.get(Calendar.YEAR);

        buf.append(day);
        buf.append(" ");
        buf.append(month);
        buf.append(" ");
        buf.append(year);
        return buf.toString();

    }

    // ///////////////////////////////////////////////////////////////////////
    //
    // The Following functions are used to get the Media Images of a Place.
    //
    // ///////////////////////////////////////////////////////////////////////
    /**
     * Make a request for a page of media images.
     *
     * @param requestMore whether we are requesting more media images of the
     * current place or not.
     */
    public void makeImageRequest(boolean requestMore) {

        deleteAll();
        clearMenuItems();
        addTitle();

        // An asynchronous request is made success and failure are
        // handled in the two callback functions described below.

        if (!requestMore) {
            // Create a request modifier, and specify image dimensions
            Modifier modifier = PlaceFactory.getInstance().createRequestModifier();

            modifier.addImageDimensions(new Integer(256), null);
            // Make the request for a page of media images
            ImagePageRequest request = sf.createImagePageRequest(modifier);

            request.getImages(place.getId(), this);
        } else {
            mediaImages.nextPage(this);
        }

    }

    /**
     * Called when media image request has successfully completed and a valid
     * result obtained.
     *
     * @param request the request that effected the query for a page of images.
     * @param result a paginated collection of places that resulted from the
     * search
     */
    public void onRequestComplete(ImagePageRequest request, ImagePage result) {
        mediaImages = result;
        com.nokia.places.media.Image[] images = result.getItems();

        for (int i = 0; i < images.length; i++) {
            addImage(images[i]);
        }

        addMenuItem(mediaImages.hasNextPage(), MORE_IMAGES);

    }

    /**
     * Displays the media image data on screen.
     * @param mediaImage
     */
    private void addImage(com.nokia.places.media.Image mediaImage) {
        // Discover the keys of the scaled images requested.
        // and use these keys to obtain the scaled images
        String[] dimensions = mediaImage.getDimensions();
        javax.microedition.lcdui.Image thumbnail = mediaImage.getScaledImage(
                dimensions[0]);

        append(
                new ImageItem("", thumbnail, ImageItem.LAYOUT_DEFAULT, "",
                Item.PLAIN));
        append(
                new StringItem("Attribution",
                mediaImage.getAttributionText().getText(), Item.PLAIN));
    }

    /**
     * Something strange has occurred when requesting an image.
     *
     * @param request the request that effected the query for a page of images.
     * @param error the detail for the source of the error
     */
    public void onRequestError(ImagePageRequest request, Throwable error) {
        // <b>Very</b> Simple Error handling
        System.out.println(error.toString());
    }

    /**
     * Creates a simple hypertext link without title or link text.
     * @param url
     * @return the StringItem representing this URL
     */
    private StringItem hypertextLink(String url) {
        return hypertextLink("", url, url);
    }

    /**
     * Creates a simple hypertext link with title and link text.
     * @param label
     * @param text
     * @param url
     * @return the StringItem representing this URL
     */
    private StringItem hypertextLink(String label, String text, String url) {
        StringItem link = new URLItem(label, text, url, Item.HYPERLINK);

        link.setDefaultCommand(HypertextLinkRunner.COMMAND);
        link.setItemCommandListener(hypertextRunner);
        return link;
    }

}
