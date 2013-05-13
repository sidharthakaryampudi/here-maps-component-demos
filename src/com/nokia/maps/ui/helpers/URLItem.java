
package com.nokia.maps.ui.helpers;


import javax.microedition.lcdui.StringItem;


/**
 * This class defines a StringItem which is to be used as a link
 */
public class URLItem extends StringItem {

    private final String URL;

    public URLItem(String label, String text, String URL) {
        super(label, text);
        this.URL = URL;
    }

    public URLItem(String label, String text, String URL, int appearanceMode) {
        super(label, text, appearanceMode);
        this.URL = URL;
    }

    /**
     * @return the URL
     */
    public String getURL() {
        return URL;
    }
}
