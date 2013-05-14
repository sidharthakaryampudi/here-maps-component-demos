/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.sharing;


import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.example.BaseMIDlet;
import com.nokia.maps.example.MapCanvasExample;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapDisplayState;
import com.nokia.maps.map.Point;
import com.nokia.maps.sharing.SharingManager;


/**
 * Minimal MIDP application to show map content to the user.
 */
public class SharingMIDlet extends BaseMIDlet {

    protected MapCanvas getDemo(Display display) {

        SharingConfig messageConfig = new SharingConfig();

        return new SharingExample(display, this, messageConfig);
    }

    protected String getTitle() {
        return "Sharing Example";
    }

    protected String getDescription() {
        return "This example shows how share a location using SMS messaging ";
    }

    /**
     * Demonstrates adding moving the map to a location and sharing this
     * location via a URL
     */
    private class SharingExample extends MapCanvasExample {

        private final Command SEND_SMS = new Command("Share via SMS", Command.OK,
                1);
        private final SharingConfig config;
        private final GeoCoordinate BERLIN = new GeoCoordinate(52.5310, 13.3849,
                0);

        public SharingExample(Display display, MIDlet midlet,
                SharingConfig config) {
            super(display, midlet);
            this.config = config;
            addCommand(SEND_SMS);
            config.setCommandListener(getCommandListener());
            // Set up the map, this will initially display a map of central Berlin
            map.setState(new MapDisplayState(BERLIN, 13));
        }

        /**
         * Adds a marker to centre of the screen and returns its coordinate
         *
         * @return coordinate added
         */
        private GeoCoordinate selectPosition() {
            Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);
            GeoCoordinate gc = map.pixelToGeo(center);

            return gc;
        }

        /**
         * Called from thread executing the command
         */
        public void commandRun(Command c) {
            if (c == SEND_SMS) {
                selectPosition();
                SharingManager sm = SharingManager.getInstance();
                String url = sm.getMapUrl(map);

                config.setPhoneNumber("");
                config.setMessage(url);
                display.setCurrent(config);
            } else if (c == SharingConfig.CANCEL) {
                display.setCurrent(this);
            } else if (c == SharingConfig.SEND) {
                String message = config.getMessage();
                String phoneNumber = config.getPhoneNumber();

                if (sendSms(phoneNumber, message)) {
                    note("Sent to " + phoneNumber, 2500);
                } else {
                    note("Could not send to " + phoneNumber, 2500);
                }
                display.setCurrent(this);
            }
        }

        // From FNDN.
        // http://www.developer.nokia.com/Community/Wiki/CS000976_-_Sending_a_text_SMS
        public boolean sendSms(String number, String message) {
            boolean result = true;

            try {
                String addr = "sms://" + number;
                MessageConnection conn = (MessageConnection) Connector.open(addr);
                TextMessage msg = (TextMessage) conn.newMessage(
                        MessageConnection.TEXT_MESSAGE);

                msg.setPayloadText(message);
                conn.send(msg);
                conn.close();
            } catch (SecurityException se) {
                result = false;
            } catch (Exception e) {
                result = false;
            }
            return result;
        }
    }
}
